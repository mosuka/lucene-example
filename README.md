# lucene-example

## Examples

### Add document

```
$ java -jar lucene-example.jar add -i /tmp/index -d '{"id":"1","title":"Lucene","description":"Lucene is an OSS."}'
```

### Search documents

```
$ java -jar lucene-example.jar search -i /tmp/index -q title:Lucene
```

### Update document

```
$ java -jar lucene-example.jar update -i /tmp/index -d '{"id":"1","title":"Lucene","description":"Lucene is a Full-text search library."}'
```

### Delete document

```
$ java -jar lucene-example.jar delete -i /tmp/index -v 1
```