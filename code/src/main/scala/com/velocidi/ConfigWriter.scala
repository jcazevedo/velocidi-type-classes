package com.velocidi

import scala.collection.JavaConverters._
import scala.language.higherKinds

import com.typesafe.config.{ ConfigObject, ConfigValue, ConfigValueFactory }
import shapeless._
import shapeless.labelled._

trait ConfigWriter[A] {
  def write(value: A): ConfigValue
}

object ConfigWriter extends BasicWriters with CollectionWriters with DerivedWriters {
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

trait DerivedWriters {
  implicit val hNilWriter: ConfigWriter[HNil] = new ConfigWriter[HNil] {
    def write(value: HNil): ConfigValue = ConfigValueFactory.fromMap(Map.empty[String, Any].asJava)
  }

  implicit def hListWriter[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hWriter: ConfigWriter[H],
    tWriter: ConfigWriter[T]): ConfigWriter[FieldType[K, H] :: T] =
    new ConfigWriter[FieldType[K, H] :: T] {
      def write(value: FieldType[K, H] :: T): ConfigValue = {
        val obj = tWriter.write(value.tail).asInstanceOf[ConfigObject]
        val key = witness.value.name
        obj.withValue(key, hWriter.write(value.head))
      }
    }

  implicit val cNilWriter: ConfigWriter[CNil] = new ConfigWriter[CNil] {
    def write(value: CNil): ConfigValue = ???
  }

  implicit def coproductWriter[K <: Symbol, H, T <: Coproduct](
    implicit
    witness: Witness.Aux[K],
    hWriter: ConfigWriter[H],
    tWriter: ConfigWriter[T]): ConfigWriter[FieldType[K, H] :+: T] =
    new ConfigWriter[FieldType[K, H] :+: T] {
      def write(value: FieldType[K, H] :+: T): ConfigValue = value match {
        case Inl(head) => hWriter.write(head)
        case Inr(tail) => tWriter.write(tail)
      }
    }

  implicit def productWriter[A, Repr](
    implicit
    gen: LabelledGeneric.Aux[A, Repr],
    reprWriter: ConfigWriter[Repr]): ConfigWriter[A] = new ConfigWriter[A] {
    def write(value: A): ConfigValue = reprWriter.write(gen.to(value))
  }
}
