package com.velocidi

import com.typesafe.config.{ ConfigValue, ConfigValueFactory }

trait ConfigWriter[A] {
  def write(value: A): ConfigValue
}

object ConfigWriter extends BasicWriters {
  object Ops {
    implicit class ConfigWriterOps[A: ConfigWriter](x: A) {
      def toConfig: ConfigValue =
        implicitly[ConfigWriter[A]].write(x)
    }
  }
}

trait BasicWriters {
  implicit val intWriter: ConfigWriter[Int] = new ConfigWriter[Int] {
    def write(value: Int): ConfigValue = ConfigValueFactory.fromAnyRef(value)
  }

  implicit val longWriter: ConfigWriter[Long] = new ConfigWriter[Long] {
    def write(value: Long): ConfigValue = ConfigValueFactory.fromAnyRef(value)
  }

  implicit val doubleWriter: ConfigWriter[Double] = new ConfigWriter[Double] {
    def write(value: Double): ConfigValue = ConfigValueFactory.fromAnyRef(value)
  }

  implicit val stringWriter: ConfigWriter[String] = new ConfigWriter[String] {
    def write(value: String): ConfigValue = ConfigValueFactory.fromAnyRef(value)
  }

  implicit val booleanWriter: ConfigWriter[Boolean] = new ConfigWriter[Boolean] {
    def write(value: Boolean): ConfigValue = ConfigValueFactory.fromAnyRef(value)
  }
}
