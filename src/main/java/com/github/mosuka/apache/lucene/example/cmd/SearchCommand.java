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
package com.github.mosuka.apache.lucene.example.cmd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codehaus.jackson.map.ObjectMapper;

public class SearchCommand implements Command {

  @Override
  public void execute(Map<String, Object> attrs) {
    Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

    String responseJSON = null;
    Directory indexDir = null;
    IndexReader reader = null;

    try {
      String index = (String) attrs.get("index");
      String queryStr = (String) attrs.get("query");

      indexDir = FSDirectory.open(new File(index).toPath());

      QueryParser queryParser = new QueryParser("text", new JapaneseAnalyzer());
      Query query = queryParser.parse(queryStr);

      reader = DirectoryReader.open(indexDir);
      IndexSearcher searcher = new IndexSearcher(reader);

      TopDocs topDocs = searcher.search(query, 10);

      List<Map<String, Object>> documentList =
          new LinkedList<Map<String, Object>>();
      for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        Document document = searcher.doc(scoreDoc.doc);

        Map<String, Object> documentMap = new LinkedHashMap<String, Object>();
        for (IndexableField f : document.getFields()) {
          documentMap.put(f.name(), f.stringValue());
        }
        documentMap.put("score", scoreDoc.score);
        documentList.add(documentMap);
      }

      responseMap.put("status", 0);
      responseMap.put("message", "OK");
      responseMap.put("totalHits", topDocs.totalHits);
      responseMap.put("maxScore", topDocs.getMaxScore());
      responseMap.put("result", documentList);
    } catch (IOException e) {
      responseMap.put("status", -1);
      responseMap.put("message", e.getMessage());
    } catch (ParseException e) {
      responseMap.put("status", -1);
      responseMap.put("message", e.getMessage());
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        responseMap.put("status", -1);
        responseMap.put("message", e.getMessage());
      }
      try {
        if (indexDir != null) {
          indexDir.close();
        }
      } catch (IOException e) {
        responseMap.put("status", -1);
        responseMap.put("message", e.getMessage());
      }
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      responseJSON = mapper.writeValueAsString(responseMap);
    } catch (IOException e) {
      responseJSON =
          String.format("{\"status\":-1, \"message\":\"%s\"}", e.getMessage());
    }
    System.out.println(responseJSON);
  }

}
