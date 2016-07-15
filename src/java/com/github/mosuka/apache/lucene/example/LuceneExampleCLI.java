/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mosuka.apache.lucene.example;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class LuceneExampleCLI {
  public enum Command {
    ADD("add"),
    UPDATE("update"),
    DELETE("delete"),
    SEARCH("search"),
    UNKNOWN("unknown");

    private final String commandStr;

    private Command(String commandStr) {
      this.commandStr = commandStr;
    }

    public static Command toCommand(String commandStr) {
      Command command = null;
      for (Command tmp : values()) {
        if (tmp.commandStr.equals(commandStr)) {
          command = tmp;
          break;
        }
        command = UNKNOWN;
      }
      return command;
    }

    public String getString() {
      return this.commandStr;
    }
  }

  public static Document createDocument(String dataStr) throws JsonParseException, JsonMappingException, IOException {
    Map<String, Object> dataMap = new ObjectMapper().readValue(dataStr, new TypeReference<HashMap<String, Object>>() {});

    Document document = new Document();
    if (dataMap.containsKey("id")) {
      document.add(new TextField("id", dataMap.get("id").toString(), Store.YES));
    }
    if (dataMap.containsKey("title")) {
      document.add(new TextField("title", dataMap.get("title").toString(), Store.YES));
    }
    if (dataMap.containsKey("description")) {
      document.add(new TextField("description", dataMap.get("description").toString(), Store.YES));
    }

    return document;
  }

  public static void main(String[] args) {
    Command command = Command.toCommand(args[0]);

    Options options = new Options();
    options.addOption(new Option("h", "help", false, "Show this message."));
    options.addOption(new Option("i", "index", true, "Index directory path."));
    options.addOption(new Option("d", "data", true, "Document data formatted using JSON."));
    options.addOption(new Option("q", "query", true, "Query to search index."));

    DefaultParser defaultParser = new DefaultParser();
    HelpFormatter helpFormatter = new HelpFormatter();
    String commandLineSyntax = "java -jar lucene-example.jar [command] [Options]";
    StringBuilder commandLineHeader = new StringBuilder()
        .append("\nAvailable command:\n")
        .append(" add                Add data.\n")
        .append(" update             Update data.\n")
        .append(" delete             Delete data.\n")
        .append(" search             Search data.\n")
        .append("\nOprions:");
    StringBuilder commandLineFooter = new StringBuilder()
        .append("\nExamples:\n")
        .append(" java -jar example.jar add  -i ./index -d '{\"id\":\"1\", \"title\":\"Lucene\", \"description\":\"Lucene is a full-text serch library implemented in Java.\"}'\n")
        .append(" java -jar example.jar update -i ./index -d '{\"id\":\"1\", \"title\":\"Lucene\", \"description\":\"Lucene is an open source project available for free download.\"}'\n")
        .append(" java -jar example.jar delete -i ./index -d '{\"id\":\"1\"}'\n")
        .append(" java -jar example.jar search -i ./index -q \"Lucene\"\n");

    String pathStr = null;
    String dataSrt = null;
    String queryStr = null;
    
    try {
      CommandLine commandLine = defaultParser.parse(options, args);
      if (commandLine.hasOption("h")) {
        helpFormatter.printHelp(-1, commandLineSyntax, commandLineHeader.toString(), options, commandLineFooter.toString());
        return;
      }
      if (commandLine.hasOption("i")) {
        pathStr = commandLine.getOptionValue("i");
      }
      if (commandLine.hasOption("d")) {
        dataSrt = commandLine.getOptionValue("d");
      }
      if (commandLine.hasOption("q")) {
        queryStr = commandLine.getOptionValue("q");
      }
    } catch (ParseException e) {
      helpFormatter.printHelp(-1, commandLineSyntax, commandLineHeader.toString(), options, commandLineFooter.toString());
    }

    Directory indexDir = null;

    Analyzer standardAnalyzer = new StandardAnalyzer();
    Analyzer keywordAnalyzer = new KeywordAnalyzer();
    Analyzer japaneseAnalyzer = new JapaneseAnalyzer();
    Map<String, Analyzer> analyzerMap = new HashMap<>();
    analyzerMap.put("id", keywordAnalyzer);
    analyzerMap.put("title", japaneseAnalyzer);
    analyzerMap.put("description", japaneseAnalyzer);
    PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(standardAnalyzer, analyzerMap);

    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(OpenMode.CREATE_OR_APPEND);

    IndexWriter writer = null;
    IndexReader reader = null;
    IndexSearcher searcher = null;

    try {
      indexDir = FSDirectory.open(new File(pathStr).toPath());

      switch (command) {
      case ADD:
        try {
          writer = new IndexWriter(indexDir, config);
          Document document = createDocument(dataSrt);
          writer.addDocument(document);
          writer.commit();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          writer.close();
        }
        break;
      case UPDATE:
        try {
          writer = new IndexWriter(indexDir, config);
          Document document = createDocument(dataSrt);
          writer.updateDocument(new Term("id", document.get("id")), document);
          writer.commit();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (writer != null) {
            writer.close();
          }
        }
        break;
      case DELETE:
        try {
          writer = new IndexWriter(indexDir, config);
          Document document = createDocument(dataSrt);
          writer.deleteDocuments(new Term("id", document.get("id")));
          writer.commit();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (writer != null) {
            writer.close();
          }
        }
        break;
      case SEARCH:
        try {
          reader = DirectoryReader.open(indexDir);
          searcher = new IndexSearcher(reader);

          QueryParser queryParser = new QueryParser("title", japaneseAnalyzer);
          Query query = queryParser.parse(queryStr);
          
          TopDocs topDocs = searcher.search(query, 10);
          
          for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(
                String.format(
                    "id: %s, title: %s, description: %s, score:%f",
                    document.getField("id").stringValue(),
                    document.getField("title").stringValue(),
                    document.getField("description").stringValue(),
                    scoreDoc.score));
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
          e.printStackTrace();
        } finally {
          if (reader != null) {
            reader.close();
          }
        }
        break;
      default:
        helpFormatter.printHelp(-1, commandLineSyntax, commandLineHeader.toString(), options, commandLineFooter.toString());
        break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (indexDir != null) {
          indexDir.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
