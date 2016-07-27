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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

public class SearchCommandTest extends TestCase {
  private String index;

  private ByteArrayOutputStream _baos;
  private PrintStream _out;

  @Before
  public void setUp() {
    this.index = System.getProperty("java.io.tmpdir");
    String uniqueId = "1";
    String text = "Lucene is an open source software.";

    Map<String, Object> addAttrs = new HashMap<String, Object>();
    addAttrs.put("index", this.index);
    addAttrs.put("unique_id", uniqueId);
    addAttrs.put("text", text);

    AddCommand addCommand = new AddCommand();
    addCommand.execute(addAttrs);

    _baos = new ByteArrayOutputStream();
    _out = System.out;
    System.setOut(new PrintStream(new BufferedOutputStream(_baos)));
  }

  @After
  public void tearDown() {
    System.setOut(_out);
  }

  public void testExecute() throws JsonParseException, JsonMappingException, IOException {
    String index = this.index;
    String query = "text:Lucene";

    Map<String, Object> searchAttrs = new HashMap<String, Object>();
    searchAttrs.put("index", index);
    searchAttrs.put("query", query);

    SearchCommand searchCommand = new SearchCommand();
    searchCommand.execute(searchAttrs);

    System.out.flush();

    String expected =
        "{\"status\":0,\"message\":\"OK\",\"result\":[{\"id\":\"1\",\"text\":\"Lucene is an open source software\"}]}\n";
    String actual = _baos.toString();

    Map<String, Object> expectedList =
        new ObjectMapper().readValue(expected, new TypeReference<HashMap<String, Object>>() {
        });

    Map<String, Object> actualList =
        new ObjectMapper().readValue(actual, new TypeReference<HashMap<String, Object>>() {
        });

    assertEquals(expectedList.get("status"), actualList.get("status"));
  }
}
