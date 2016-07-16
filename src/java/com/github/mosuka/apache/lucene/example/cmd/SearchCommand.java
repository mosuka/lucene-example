package com.github.mosuka.apache.lucene.example.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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

public class SearchCommand implements Command{
  @Override
  public void execute(Map<String, Object> attrs) {
    Directory indexDir = null;
    
    try {
      indexDir = FSDirectory.open(new File((String)attrs.get("index")).toPath());

      IndexReader reader = null;
      try {
        reader = DirectoryReader.open(indexDir);
        
        IndexSearcher searcher = new IndexSearcher(reader);

        QueryParser queryParser = new QueryParser("title", new JapaneseAnalyzer());
        Query query = queryParser.parse((String)attrs.get("query"));

        TopDocs topDocs = searcher.search(query, 10);

        List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
          Document document = searcher.doc(scoreDoc.doc);
          Map<String, Object> documentMap = new LinkedHashMap<String, Object>();
          for (Iterator<IndexableField> i = document.iterator(); i.hasNext(); ) {
            IndexableField f = i.next();
            documentMap.put(f.name(), f.stringValue());
          }
          result.add(documentMap);
        }
        
        System.out.println(new ObjectMapper().writeValueAsString(result));
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ParseException e) {
        e.printStackTrace();
      } finally {
        if (reader != null) {
          reader.close();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        indexDir.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}