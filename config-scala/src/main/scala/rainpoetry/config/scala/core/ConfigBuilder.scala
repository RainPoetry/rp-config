package rainpoetry.config.scala.core

import java.util.concurrent.TimeUnit

import rainpoetry.config.scala.core.entries._
import rainpoetry.config.scala.tools.ByteUnit

import scala.util.matching.Regex

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    Config 声明的接口口
 */

case class ConfigBuilder(key: String) {

  import ConfigHelpers._

  private[config] var _public = true
  private[config] var _doc = ""
  private[config] var _onCreate: Option[ConfigEntry[_] => Unit] = None
  private[config] var _alternatives = List.empty[String]

  def internal(): ConfigBuilder = {
    _public = false
    this
  }

  def doc(s: String): ConfigBuilder = {
    _doc = s
    this
  }

  /**
   * Registers a callback for when the config entry is finally instantiated. Currently used by
   * SQLConf to keep track of SQL configuration entries.
   */
  def onCreate(callback: ConfigEntry[_] => Unit): ConfigBuilder = {
    _onCreate = Option(callback)
    this
  }

  def withAlternative(key: String): ConfigBuilder = {
    _alternatives = _alternatives :+ key
    this
  }

  def intConf: TypedConfigBuilder[Int] = {
    new TypedConfigBuilder(this, toNumber(_, _.toInt, key, "int"))
  }

  def longConf: TypedConfigBuilder[Long] = {
    new TypedConfigBuilder(this, toNumber(_, _.toLong, key, "long"))
  }

  def doubleConf: TypedConfigBuilder[Double] = {
    new TypedConfigBuilder(this, toNumber(_, _.toDouble, key, "double"))
  }

  def booleanConf: TypedConfigBuilder[Boolean] = {
    new TypedConfigBuilder(this, toBoolean(_, key))
  }

  def stringConf: TypedConfigBuilder[String] = {
    new TypedConfigBuilder(this, v => v)
  }

  def timeConf(unit: TimeUnit): TypedConfigBuilder[Long] = {
    new TypedConfigBuilder(this, timeFromString(_, unit), timeToString(_, unit))
  }

  def bytesConf(unit: ByteUnit): TypedConfigBuilder[Long] = {
    new TypedConfigBuilder(this, byteFromString(_, unit), byteToString(_, unit))
  }

  def fallbackConf[T](fallback: ConfigEntry[T]): ConfigEntry[T] = {
    val entry = new FallbackConfigEntry(key, _alternatives, _doc, fallback)
    _onCreate.foreach(_(entry))
    entry
  }

  def regexConf: TypedConfigBuilder[Regex] = {
    new TypedConfigBuilder(this, regexFromString(_, this.key), _.toString)
  }
}
