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
}
