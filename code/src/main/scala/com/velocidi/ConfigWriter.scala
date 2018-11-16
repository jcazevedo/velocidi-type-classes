package com.velocidi

import scala.collection.JavaConverters._
import scala.language.higherKinds

import com.typesafe.config.{ ConfigValue, ConfigValueFactory }

trait ConfigWriter[A] {
  def write(value: A): ConfigValue
}

object ConfigWriter extends BasicWriters with CollectionWriters {
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

trait CollectionWriters {
  implicit def traversableWriter[A, F[A] <: TraversableOnce[A]](
    implicit
    writer: ConfigWriter[A]): ConfigWriter[F[A]] = new ConfigWriter[F[A]] {
    def write(value: F[A]): ConfigValue =
      ConfigValueFactory.fromIterable(value.toList.map(writer.write).asJava)
  }

  implicit def mapWriter[A](
    implicit
    writer: ConfigWriter[A]): ConfigWriter[Map[String, A]] = new ConfigWriter[Map[String, A]] {
    def write(value: Map[String, A]): ConfigValue =
      ConfigValueFactory.fromMap(value.mapValues(writer.write).asJava)
  }
}
