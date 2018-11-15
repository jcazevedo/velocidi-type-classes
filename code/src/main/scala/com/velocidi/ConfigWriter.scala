package com.velocidi

import com.typesafe.config.ConfigValue

trait ConfigWriter[A] {
  def write(value: A): ConfigValue
}

object ConfigWriter {
  object Ops {
    implicit class ConfigWriterOps[A: ConfigWriter](x: A) {
      def toConfig: ConfigValue =
        implicitly[ConfigWriter[A]].write(x)
    }
  }
}
