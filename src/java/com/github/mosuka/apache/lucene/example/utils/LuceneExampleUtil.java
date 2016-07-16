package com.github.mosuka.apache.lucene.example.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class LuceneExampleUtil {
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
  
  public static Analyzer createAnalyzerWrapper() {
    Analyzer standardAnalyzer = new StandardAnalyzer();
    Analyzer keywordAnalyzer = new KeywordAnalyzer();
    Analyzer japaneseAnalyzer = new JapaneseAnalyzer();
    Map<String, Analyzer> analyzerMap = new HashMap<>();
    analyzerMap.put("id", keywordAnalyzer);
    analyzerMap.put("title", japaneseAnalyzer);
    analyzerMap.put("description", japaneseAnalyzer);
    
    PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(standardAnalyzer, analyzerMap);
    
    return analyzerWrapper;
  }
}
