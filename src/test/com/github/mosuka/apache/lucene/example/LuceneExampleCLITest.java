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

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import junit.framework.TestCase;

public class LuceneExampleCLITest extends TestCase {
  public void testCreateDocument() throws JsonParseException, JsonMappingException, IOException {
    String dataStr = "{\"id\":\"1\", \"title\":\"Lucene\", \"description\":\"Lucene is a full-text serch library implemented in Java.\"}";
    Document document = LuceneExampleCLI.createDocument(dataStr);

    String expectedId = "1";
    String actualId = document.getField("id").stringValue();
    assertEquals(expectedId, actualId);

    String expectedTitle = "Lucene";
    String actualTitle = document.getField("title").stringValue();
    assertEquals(expectedTitle, actualTitle);

    String expectedDescription = "Lucene is a full-text serch library implemented in Java.";
    String actualDescription = document.getField("description").stringValue();
    assertEquals(expectedDescription, actualDescription);
  }
}
