package com.velocidi

import com.typesafe.config.ConfigValue

trait ConfigReader[A] {
  def read(configValue: ConfigValue): A
}

object ConfigReader extends BasicReaders {
  object Ops {
    implicit class ConfigReaderOps(x: ConfigValue) {
      def as[A: ConfigReader]: A =
        implicitly[ConfigReader[A]].read(x)
    }
  }
}

trait BasicReaders {
  implicit val intReader: ConfigReader[Int] = new ConfigReader[Int] {
    def read(configValue: ConfigValue): Int = configValue.unwrapped.asInstanceOf[Int]
  }

  implicit val longReader: ConfigReader[Long] = new ConfigReader[Long] {
    def read(configValue: ConfigValue): Long = configValue.unwrapped.asInstanceOf[Long]
  }

  implicit val doubleReader: ConfigReader[Double] = new ConfigReader[Double] {
    def read(configValue: ConfigValue): Double = configValue.unwrapped.asInstanceOf[Double]
  }

  implicit val stringReader: ConfigReader[String] = new ConfigReader[String] {
    def read(configValue: ConfigValue): String = configValue.unwrapped.asInstanceOf[String]
  }

  implicit val booleanReader: ConfigReader[Boolean] = new ConfigReader[Boolean] {
    def read(configValue: ConfigValue): Boolean = configValue.unwrapped.asInstanceOf[Boolean]
  }
}
