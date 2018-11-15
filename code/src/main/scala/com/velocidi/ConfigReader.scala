package com.velocidi

import scala.collection.JavaConverters._
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

import com.typesafe.config.{ ConfigList, ConfigValue }

trait ConfigReader[A] {
  def read(configValue: ConfigValue): A
}

object ConfigReader extends BasicReaders with CollectionReaders {
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

trait CollectionReaders {
  implicit def traversableReader[A, F[A] <: TraversableOnce[A]](
    implicit
    reader: ConfigReader[A],
    cbf: CanBuildFrom[F[A], A, F[A]]): ConfigReader[F[A]] = new ConfigReader[F[A]] {
    def read(configValue: ConfigValue): F[A] =
      configValue.asInstanceOf[ConfigList].asScala.foldLeft(cbf()) {
        case (acc, x) => acc += reader.read(x)
      }.result()
  }
}
