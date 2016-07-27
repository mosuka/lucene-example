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

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import junit.framework.TestCase;

public class LuceneExampleUtilTest extends TestCase {

  public void testCreateDocument() throws JsonParseException, JsonMappingException, IOException {
    String id = "1";
    String text = "Lucene is a full-text serch library implemented in Java.";

    Document document = LuceneExampleUtil.createDocument(id, text);

    String expectedId = "1";
    String actualId = document.getField("id").stringValue();
    assertEquals(expectedId, actualId);

    String expectedText = "Lucene is a full-text serch library implemented in Java.";
    String actualText = document.getField("text").stringValue();
    assertEquals(expectedText, actualText);
  }

  public void testCreateAnalyzerWrapper() throws IOException {
    PerFieldAnalyzerWrapper wrapper = LuceneExampleUtil.createAnalyzerWrapper();

    TokenStream tokenStream = null;
    CharTermAttribute charTermAttribute = null;

    List<String> expectedIdTermList = new LinkedList<String>(Arrays.asList("1"));
    List<String> actualIdTermList = new LinkedList<String>();
    tokenStream = wrapper.tokenStream("id", "1");
    charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    while (tokenStream.incrementToken()) {
      actualIdTermList.add(charTermAttribute.toString());
    }
    tokenStream.close();
    assertEquals(expectedIdTermList, actualIdTermList);

    List<String> expectedTextTermList = new LinkedList<String>(
        Arrays.asList("lucene", "is", "a", "full", "text", "search", "library"));
    List<String> actualTextTermList = new LinkedList<String>();
    tokenStream = wrapper.tokenStream("text", "Lucene is a Full-text search library.");
    charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    while (tokenStream.incrementToken()) {
      actualTextTermList.add(charTermAttribute.toString());
    }
    tokenStream.close();
    assertEquals(expectedTextTermList, actualTextTermList);
  }

}
