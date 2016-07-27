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
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codehaus.jackson.map.ObjectMapper;

import com.github.mosuka.apache.lucene.example.utils.LuceneExampleUtil;

public class UpdateCommand implements Command {

  @Override
  public void execute(Map<String, Object> attrs) {
    Map<String, Object> responseMap = new LinkedHashMap<String, Object>();

    String responseJSON = null;
    Directory indexDir = null;
    IndexWriter writer = null;

    try {
      String index = (String) attrs.get("index");
      String uniqueId = (String) attrs.get("unique_id");
      String text = (String) attrs.get("text");

      indexDir = FSDirectory.open(new File(index).toPath());

      Document document = LuceneExampleUtil.createDocument(uniqueId, text);

      IndexWriterConfig config =
          new IndexWriterConfig(LuceneExampleUtil.createAnalyzerWrapper());
      config.setOpenMode(OpenMode.CREATE_OR_APPEND);

      writer = new IndexWriter(indexDir, config);
      writer.updateDocument(new Term("id", document.get("id")), document);
      writer.commit();

      responseMap.put("status", 0);
      responseMap.put("message", "OK");
    } catch (IOException e) {
      responseMap.put("status", -1);
      responseMap.put("message", e.getMessage());
    } finally {
      try {
        if (writer != null) {
          writer.close();
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
