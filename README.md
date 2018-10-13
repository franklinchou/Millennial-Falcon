# Millennial-Falcon

Just your basic ass, Starbucks drinking, social-media-consuming next-gen entitlements software


## Running locally

1. Starting Cassandra:

* `docker run --name cassandra-test -d cassandra:latest  # first time`
* `docker start cassandra-test`

2. Running Cassandra commands:

* `docker exec -it cassandra-test bash`

3. Start server locally by issuing `sbt run` in the `server/` directory.
