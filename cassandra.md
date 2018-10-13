# Cassandra

## Executing commands with cqlsh

1. Using the correct collection

* `describe keyspaces;`
* `use <keyspace>;`
* `describe tables;`  # list all available tables in this keyspace
* `use <table>;`

2. Dropping a keyspace

* `drop keyspace <keyspace>;`
* Note: The project can be reverted by issuing: `drop keyspace janusgraph`.
