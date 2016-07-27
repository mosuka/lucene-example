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
package com.github.mosuka.apache.lucene.example.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class LuceneExampleUtil {

  public static Map<String, Analyzer> getAnalyzerMap() {
    Analyzer keywordAnalyzer = new KeywordAnalyzer();
    Analyzer japaneseAnalyzer = new JapaneseAnalyzer();

    Map<String, Analyzer> analyzerMap = new HashMap<>();
    analyzerMap.put("id", keywordAnalyzer);
    analyzerMap.put("text", japaneseAnalyzer);

    return analyzerMap;
  }

  public static Document createDocument(String id, String text) {
    Document document = new Document();
    document.add(new StringField("id", id, Store.YES));
    document.add(new TextField("text", text, Store.YES));

    return document;
  }

  public static PerFieldAnalyzerWrapper createAnalyzerWrapper() {
    PerFieldAnalyzerWrapper analyzerWrapper = new PerFieldAnalyzerWrapper(
        getAnalyzerMap().get("text"), getAnalyzerMap());

    return analyzerWrapper;
  }

}
