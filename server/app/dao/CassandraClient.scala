package dao

import com.datastax.driver.core.{Cluster, Session}
import com.typesafe.config.ConfigFactory

object CassandraClient {

  val address = ConfigFactory.load.getString("cassandra.host")

  val port = ConfigFactory.load.getInt("cassandra.port")

  private val cluster =
    Cluster
      .builder
      .addContactPoint(address)
      .withPort(port)
      .build()

  val session: Session = cluster.connect()

}
