# lucene-example

## Examples

### Show help

```
$ java -jar lucene-example.jar -h
usage: java lucene-example.jar [-h] COMMAND ...

optional arguments:
  -h, --help             show this help message and exit

Available Commands:
  COMMAND
    add                  Add text to index.
    update               Update text to index by unique ID.
    delete               Delete text from index by unique ID.
    search               Search text of index by query.
```

### Add document

```
$ java -jar lucene-example.jar add -i /tmp/index -u 1 -t 'Lucene is an open source software.'
```

### Search documents

```
$ java -jar lucene-example.jar search -i /tmp/index -q Lucene
```

### Update document

```
$ java -jar lucene-example.jar update -i /tmp/index -u 1 -t 'Lucene is a full-text search library.'
```

### Delete document

```
$ java -jar lucene-example.jar delete -i /tmp/index -u 1
```