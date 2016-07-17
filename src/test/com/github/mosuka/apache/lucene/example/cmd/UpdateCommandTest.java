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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

public class UpdateCommandTest extends TestCase {
  private ByteArrayOutputStream _baos;
  private PrintStream _out;

  @Before
  public void setUp() {
    _baos = new ByteArrayOutputStream();
    _out = System.out;
    System.setOut(
        new PrintStream(
            new BufferedOutputStream(_baos)
            )
        );
  }

  @After
  public void tearDown() {
    System.setOut(_out);
  }

  public void testExecute() {
    String indexPath = System.getProperty("java.io.tmpdir");

    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put("index", indexPath);
    attrs.put("data", "{\"id\":\"1\",\"title\":\"Lucene\",\"description\":\"Lucene is a OSS.\"}");

    UpdateCommand updateCommand = new UpdateCommand();
    updateCommand.execute(attrs);

    System.out.flush();

    String expected = "{\"status\":\"OK\"}\n";
    String actual = _baos.toString();
    assertEquals(expected, actual);
  }
}
