package rainpoetry.config.scala.core.entries

import rainpoetry.config.scala.core.ConfigReader

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    定义 Config 实体
 */

abstract class ConfigEntry[T](val key: String,
                     val alternatives: List[String],
                     val valueConverter: String => T,
                     val stringConverter: T => String,
                     val doc: String) {

  import ConfigEntry._

  registerEntry(this)

  def defaultValueString: String

  protected def readString(reader: ConfigReader): Option[String] = {
    alternatives.foldLeft(reader.get(key))((res, nextKey) => res.orElse(reader.get(nextKey)))
  }

  def readFrom(reader: ConfigReader): T

  def defaultValue: Option[T] = None

  override def toString: String = {
    s"ConfigEntry(key=$key, defaultValue=$defaultValueString, doc=$doc)"
  }

}

object ConfigEntry {

  val UNDEFINED = "<undefined>"

  private val knownConfigs = new java.util.concurrent.ConcurrentHashMap[String, ConfigEntry[_]]()

  def registerEntry(entry: ConfigEntry[_]): Unit = {
    val existing = knownConfigs.putIfAbsent(entry.key, entry)
    require(existing == null, s"Config entry ${entry.key} already registered!")
  }

  def findEntry(key: String): ConfigEntry[_] = knownConfigs.get(key)
}
