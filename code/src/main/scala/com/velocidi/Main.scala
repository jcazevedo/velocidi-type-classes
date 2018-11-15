package com.velocidi

import com.typesafe.config.ConfigFactory
import ConfigReader.Ops._

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
}
