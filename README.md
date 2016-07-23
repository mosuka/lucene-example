# lucene-example

## Examples

### Add document

```
$ java -jar lucene-example.jar add -i /tmp/index -u '1' -t 'Lucene is an open source software.'
```

### Search documents

```
$ java -jar lucene-example.jar search -i /tmp/index -q 'lucene'
```

### Update document

```
$ java -jar lucene-example.jar update -i /tmp/index -u '1' -t 'Lucene is a full-text search library.'
```

### Delete document

```
$ java -jar lucene-example.jar delete -i /tmp/index -u '1'
```