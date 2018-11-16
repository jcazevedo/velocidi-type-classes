package com.velocidi

import com.typesafe.config.ConfigFactory

import com.velocidi.ConfigReader.Ops._
import com.velocidi.ConfigWriter.Ops._

object Main extends App {
  val conf = ConfigFactory.parseString(
    """|{
       |  a = 1
       |  b = 1099511627776
       |  c = 4.5
       |  d = "str"
       |  e = false
       |  f = [1, 2, 3]
       |  g {
       |    a = 1
       |    b = 2
       |    c = 3
       |  }
       |}""".stripMargin)

  assert(conf.getValue("a").as[Int] == 1)
  assert(conf.getValue("b").as[Long] == 1099511627776l)
  assert(conf.getValue("c").as[Double] == 4.5)
  assert(conf.getValue("d").as[String] == "str")
  assert(conf.getValue("e").as[Boolean] == false)
  assert(conf.getValue("f").as[List[Int]] == List(1, 2, 3))
  assert(conf.getValue("f").as[Set[Int]] == Set(1, 2, 3))
  assert(conf.getValue("g").as[Map[String, Int]] == Map("a" -> 1, "b" -> 2, "c" -> 3))

  assert(1.toConfig == conf.getValue("a"))
  assert(1099511627776l.toConfig == conf.getValue("b"))
  assert(4.5.toConfig == conf.getValue("c"))
  assert("str".toConfig == conf.getValue("d"))
  assert(false.toConfig == conf.getValue("e"))
  assert(List(1, 2, 3).toConfig == conf.getValue("f"))
  assert(Set(1, 2, 3).toConfig == conf.getValue("f"))
  assert(Map("a" -> 1, "b" -> 2, "c" -> 3).toConfig == conf.getValue("g"))

  case class RabbitMQ(host: String, port: Int, username: String, password: String, defaultExchangeName: String)
  val rmq = RabbitMQ("localhost", 5672, "guest", "guest", "sf.data")

  val rabbitmqConf = ConfigFactory.parseString(
    """|{
       |  rabbitmq {
       |    host = "localhost"
       |    port = 5672
       |    username = "guest"
       |    password = "guest"
       |    defaultExchangeName = "sf.data"
       |  }
       |}""".stripMargin)

  assert(rabbitmqConf.getValue("rabbitmq").as[RabbitMQ] == rmq)
  assert(rmq.toConfig == rabbitmqConf.getValue("rabbitmq"))

  sealed trait KeyValueStore
  case class InMemory(maxSize: Int) extends KeyValueStore
  case class SqlBased(jdbcUrl: String, tableName: String) extends KeyValueStore

  val keyValueStoreConf = ConfigFactory.parseString(
    """|{
       |  in-memory {
       |    maxSize = 2000
       |  }
       |
       |  sql-based {
       |    jdbcUrl = "jdbc:h2:mem:local;DB_CLOSE_DELAY=-1"
       |    tableName = "kv-store"
       |  }
       |}""".stripMargin)

  val kvStore = InMemory(2000)
  val sqlStore = SqlBased("jdbc:h2:mem:local;DB_CLOSE_DELAY=-1", "kv-store")

  assert(keyValueStoreConf.getValue("in-memory").as[KeyValueStore] == kvStore)
  assert(keyValueStoreConf.getValue("sql-based").as[KeyValueStore] == sqlStore)
}
