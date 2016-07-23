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

import com.github.mosuka.apache.lucene.example.cmd.AddCommand;
import com.github.mosuka.apache.lucene.example.cmd.Command;
import com.github.mosuka.apache.lucene.example.cmd.DeleteCommand;
import com.github.mosuka.apache.lucene.example.cmd.SearchCommand;
import com.github.mosuka.apache.lucene.example.cmd.UpdateCommand;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class LuceneExampleCLI {

  public static void main(String[] args) {
    ArgumentParser argumentParser =
        ArgumentParsers.newArgumentParser("java lucene-example.jar");

    Subparsers commandSubpersers = argumentParser.addSubparsers()
        .title("Available Commands").metavar("COMMAND");

    Subparser addCmdSubParser = commandSubpersers.addParser("add")
        .help("Add text to index.").setDefault("command", new AddCommand());
    addCmdSubParser.addArgument("-i", "--index").help("Index path.");
    addCmdSubParser.addArgument("-u", "--unique-id").help("Unique ID.");
    addCmdSubParser.addArgument("-t", "--text").help("Text.");

    Subparser updateCmdSubParser = commandSubpersers.addParser("update")
        .help("Update text to index by unique ID.")
        .setDefault("command", new UpdateCommand());
    updateCmdSubParser.addArgument("-i", "--index").help("Index path.");
    updateCmdSubParser.addArgument("-u", "--unique-id").help("Unique ID.");
    updateCmdSubParser.addArgument("-t", "--text").help("Text.");

    Subparser deleteCmdSubParser = commandSubpersers.addParser("delete")
        .help("Delete text from index by unique ID.")
        .setDefault("command", new DeleteCommand());
    deleteCmdSubParser.addArgument("-i", "--index").help("Index path.");
    deleteCmdSubParser.addArgument("-u", "--unique-id").help("Unique ID.");

    Subparser searchCmdSubParser = commandSubpersers.addParser("search")
        .help("Search text of index by query.")
        .setDefault("command", new SearchCommand());
    searchCmdSubParser.addArgument("-i", "--index").help("Index path.");
    searchCmdSubParser.addArgument("-q", "--query").help("Query string.");

    try {
      Namespace ns = argumentParser.parseArgs(args);
      Command command = ns.get("command");
      command.execute(ns.getAttrs());
    } catch (ArgumentParserException e) {
      argumentParser.handleError(e);
    }
  }

}
