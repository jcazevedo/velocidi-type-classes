package com.velocidi

import scala.collection.JavaConverters._
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.util.Try

import com.typesafe.config.{ ConfigList, ConfigObject, ConfigValue }
import shapeless._
import shapeless.labelled._

trait ConfigReader[A] {
  def read(configValue: ConfigValue): A
}

object ConfigReader extends BasicReaders with CollectionReaders with DerivedReaders {
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

  implicit def mapReader[A](
    implicit
    reader: ConfigReader[A]): ConfigReader[Map[String, A]] = new ConfigReader[Map[String, A]] {
    def read(configValue: ConfigValue): Map[String, A] = {
      val obj = configValue.asInstanceOf[ConfigObject]
      val keys = obj.keySet()

      keys.asScala.foldLeft(Map.empty[String, A]) {
        case (acc, k) => acc + (k -> reader.read(obj.get(k)))
      }
    }
  }
}

trait DerivedReaders {
  implicit val hNilReader: ConfigReader[HNil] = new ConfigReader[HNil] {
    def read(configValue: ConfigValue): HNil = HNil
  }

  implicit def hListReader[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hReader: Lazy[ConfigReader[H]],
    tReader: Lazy[ConfigReader[T]]): ConfigReader[FieldType[K, H] :: T] =
    new ConfigReader[FieldType[K, H] :: T] {
      def read(configValue: ConfigValue): FieldType[K, H] :: T = {
        val obj = configValue.asInstanceOf[ConfigObject]
        val key = witness.value.name
        val head = obj.get(key)
        field[K](hReader.value.read(head)) :: tReader.value.read(obj.withoutKey(key))
      }
    }

  implicit val cNilReader: ConfigReader[CNil] = new ConfigReader[CNil] {
    def read(configValue: ConfigValue): CNil = ???
  }

  implicit def coproductReader[K <: Symbol, H, T <: Coproduct](
    implicit
    witness: Witness.Aux[K],
    hReader: Lazy[ConfigReader[H]],
    tReader: Lazy[ConfigReader[T]]): ConfigReader[FieldType[K, H] :+: T] =
    new ConfigReader[FieldType[K, H] :+: T] {
      def read(configValue: ConfigValue): FieldType[K, H] :+: T =
        Try(Inl(field[K](hReader.value.read(configValue)))).getOrElse(Inr(tReader.value.read(configValue)))
    }

  implicit def productReader[A, Repr](
    implicit
    gen: LabelledGeneric.Aux[A, Repr],
    reprReader: Lazy[ConfigReader[Repr]]): ConfigReader[A] = new ConfigReader[A] {
    def read(configValue: ConfigValue): A =
      gen.from(reprReader.value.read(configValue))
  }
}
