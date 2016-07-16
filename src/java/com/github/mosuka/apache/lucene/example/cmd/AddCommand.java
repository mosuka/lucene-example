package com.github.mosuka.apache.lucene.example.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.github.mosuka.apache.lucene.example.utils.LuceneExampleUtil;

public class AddCommand implements Command{
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
        writer.addDocument(document);
        writer.commit();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        writer.close();
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
