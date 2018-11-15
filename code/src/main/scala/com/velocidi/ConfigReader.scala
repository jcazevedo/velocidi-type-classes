package com.velocidi

import com.typesafe.config.ConfigValue

trait ConfigReader[A] {
  def read(configValue: ConfigValue): A
}

object ConfigReader {
  object Ops {
    implicit class ConfigReaderOps[A: ConfigReader](x: A) {
      def fromConfig(configValue: ConfigValue): A =
        implicitly[ConfigReader[A]].read(configValue)
    }
  }
}
