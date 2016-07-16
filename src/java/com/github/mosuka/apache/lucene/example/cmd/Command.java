package com.github.mosuka.apache.lucene.example.cmd;

import java.util.Map;

public interface Command {
  void execute(Map<String, Object> attrs);
}
