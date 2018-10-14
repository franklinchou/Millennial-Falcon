# Millennial-Falcon

Just your basic ass, Starbucks drinking, social-media-consuming next-gen entitlements software


## Running locally

1. Starting Cassandra:

* `docker run --name cassandra-test -d cassandra:latest  # first time`
* `docker start cassandra-test`

2. Running Cassandra commands:

Drop directly into the cassandra shell:

* `docker exec -it cassandra-test cqlsh`

Alternaively, you can run a bash prompt with:

* `docker exec -it cassandra-test bash`


3. Start server locally by issuing `sbt run` in the `server/` directory. On the first run, the graph must be setup. Enable set up mode by setting the environment variable `SETUP_MODE` to `true` (defaults to `false`).
