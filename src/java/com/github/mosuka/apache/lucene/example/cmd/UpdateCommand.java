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
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.github.mosuka.apache.lucene.example.utils.LuceneExampleUtil;

public class UpdateCommand implements Command{
  @Override
  public void execute(Map<String, Object> attrs) {
    Directory indexDir = null;
    
    IndexWriterConfig config = new IndexWriterConfig(LuceneExampleUtil.createAnalyzerWrapper());
    config.setOpenMode(OpenMode.CREATE_OR_APPEND);

    try {
      indexDir = FSDirectory.open(new File((String)attrs.get("index")).toPath());
      
      IndexWriter writer = null;
      try {
        writer = new IndexWriter(indexDir, config);
        Document document = LuceneExampleUtil.createDocument((String)attrs.get("data"));
        writer.updateDocument(new Term("id", document.get("id")), document);
        writer.commit();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (writer != null) {
          writer.close();
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
