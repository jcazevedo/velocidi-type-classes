package com.velocidi

import com.typesafe.config.{ ConfigFactory, ConfigValueFactory }

import ConfigReader.Ops._
import ConfigWriter.Ops._

object Main extends App {
  val conf = ConfigFactory.parseString(
    """|{
       |  a = 1
       |  b = 1099511627776
       |  c = 4.5
       |  d = "str"
       |  e = false
       |}""".stripMargin)

  assert(conf.getValue("a").as[Int] == 1)
  assert(conf.getValue("b").as[Long] == 1099511627776l)
  assert(conf.getValue("c").as[Double] == 4.5)
  assert(conf.getValue("d").as[String] == "str")
  assert(conf.getValue("e").as[Boolean] == false)

  assert(1.toConfig == ConfigValueFactory.fromAnyRef(1))
  assert(1099511627776l.toConfig == ConfigValueFactory.fromAnyRef(1099511627776l))
  assert(4.5.toConfig == ConfigValueFactory.fromAnyRef(4.5))
  assert("str".toConfig == ConfigValueFactory.fromAnyRef("str"))
  assert(false.toConfig == ConfigValueFactory.fromAnyRef(false))
}
