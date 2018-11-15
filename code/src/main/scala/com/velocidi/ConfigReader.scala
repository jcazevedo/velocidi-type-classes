package com.velocidi

import com.typesafe.config.ConfigValue

trait ConfigReader[A] {
  def read(configValue: ConfigValue): A
}

object ConfigReader {
  implicit class ConfigReaderOps[A: ConfigReader](x: A) {
    def fromConfig(configValue: ConfigValue): A =
      implicitly[ConfigReader[A]].read(configValue)
  }
}
