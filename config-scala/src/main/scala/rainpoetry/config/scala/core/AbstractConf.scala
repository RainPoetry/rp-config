package rainpoetry.config.scala.core

import java.util.concurrent.ConcurrentHashMap

import rainpoetry.config.scala.core.entries.{ConfigEntry, OptionalConfigEntry}
import rainpoetry.config.scala.core.providers.{AbstractConfProvider, ConfigProvider}
import rainpoetry.config.scala.tools.{ByteUnits, TimeUnits}

import scala.collection.JavaConverters._
import scala.collection.Map

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    标准的 Config 接口
 *
 *    prefix：  带有 prefix 前缀的配置无法被用户修改
 *    loadDefaults： 是否从系统变量中加载配置
 *
 *
 *    get 方法：
 *      参数是 字符串 ->  settings 来获取
 *      参数是 ConfigEntry  -> readFrom 实现
 *
 */

class AbstractConf(prefix: String = "", loadDefaults: Boolean) extends Cloneable {

  def this(prefix: String) = this(prefix, true)

  def this() = this(loadDefaults = true)

  // 是否从系统变量中加载配置
  if (loadDefaults) {
    loadFromSystemProperties
  }

  private[config] def loadFromSystemProperties: AbstractConf = {
    // Load any spark.* system properties
    for ((key, value) <- getSystemProperties if key.startsWith(prefix)) {
      set(key, value)
    }
    this
  }

  def getSystemProperties: Map[String, String] = {
    System.getProperties.stringPropertyNames().asScala
      .map(key => (key, System.getProperty(key))).toMap
  }

  private val settings = new ConcurrentHashMap[String, String]()

  @transient private lazy val reader: ConfigReader = {
    val _reader = new ConfigReader(new AbstractConfProvider(prefix, settings))
    _reader.bindEnv(new ConfigProvider {
      override def get(key: String): Option[String] = Option(getenv(key))
    })
    _reader
  }

  private[config] def getenv(name: String): String = System.getenv(name)

  def set(key: String, value: String): AbstractConf = {
    if (key == null) {
      throw new NullPointerException("null key")
    }
    if (value == null) {
      throw new NullPointerException("null value for " + key)
    }
    settings.put(key, value)
    this
  }

  private[config] def set[T](entry: ConfigEntry[T], value: T): AbstractConf = {
    set(entry.key, entry.stringConverter(value))
    this
  }

  private[config] def set[T](entry: OptionalConfigEntry[T], value: T): AbstractConf = {
    set(entry.key, entry.rawStringConverter(value))
    this
  }

  def setAll(settings: Traversable[(String, String)]): AbstractConf = {
    settings.foreach { case (k, v) => set(k, v) }
    this
  }

  def setIfMissing(key: String, value: String): AbstractConf = {
    settings.putIfAbsent(key, value)
    this
  }

  private[config] def setIfMissing[T](entry: ConfigEntry[T], value: T): AbstractConf = {
    settings.putIfAbsent(entry.key, entry.stringConverter(value))
    this
  }

  private[config] def setIfMissing[T](entry: OptionalConfigEntry[T], value: T): AbstractConf = {
    settings.putIfAbsent(entry.key, entry.rawStringConverter(value))
    this
  }

  def remove(key: String): AbstractConf = {
    settings.remove(key)
    this
  }

  private[config] def remove(entry: ConfigEntry[_]): AbstractConf = {
    remove(entry.key)
  }

  /** Get a parameter; throws a NoSuchElementException if it's not set */
  def get(key: String): String = {
    getOption(key).getOrElse(throw new NoSuchElementException(key))
  }

  /** Get a parameter, falling back to a default if not set */
  def get(key: String, defaultValue: String): String = {
    getOption(key).getOrElse(defaultValue)
  }

  def getOption(key: String): Option[String] = {
    Option(settings.get(key))
  }

  private[config] def get[T](entry: ConfigEntry[T]): T = {
    entry.readFrom(reader)
  }

  def getTimeAsSeconds(key: String): Long = catchIllegalValue(key) {
    TimeUnits.timeStringAsSec(get(key))
  }

  def getTimeAsSeconds(key: String, defaultValue: String): Long = catchIllegalValue(key) {
    TimeUnits.timeStringAsSec(get(key, defaultValue))
  }

  def getTimeAsMs(key: String): Long = catchIllegalValue(key) {
    TimeUnits.timeStringAsMs(get(key))
  }

  def getTimeAsMs(key: String, defaultValue: String): Long = catchIllegalValue(key) {
    TimeUnits.timeStringAsMs(get(key, defaultValue))
  }

  def getSizeAsBytes(key: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsBytes(get(key))
  }

  def getSizeAsBytes(key: String, defaultValue: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsBytes(get(key, defaultValue))
  }

  def getSizeAsBytes(key: String, defaultValue: Long): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsBytes(get(key, defaultValue + "B"))
  }

  def getSizeAsKb(key: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsKb(get(key))
  }

  def getSizeAsKb(key: String, defaultValue: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsKb(get(key, defaultValue))
  }

  def getSizeAsMb(key: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsMb(get(key))
  }

  def getSizeAsMb(key: String, defaultValue: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsMb(get(key, defaultValue))
  }

  def getSizeAsGb(key: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsGb(get(key))
  }

  def getSizeAsGb(key: String, defaultValue: String): Long = catchIllegalValue(key) {
    ByteUnits.byteStringAsGb(get(key, defaultValue))
  }

  def getWithSubstitution(key: String): Option[String] = {
    getOption(key).map(reader.substitute(_))
  }

  def getAll: Array[(String, String)] = {
    settings.entrySet().asScala.map(x => (x.getKey, x.getValue)).toArray
  }

  /**
   * Get a parameter as an integer, falling back to a default if not set
   *
   * @throws NumberFormatException If the value cannot be interpreted as an integer
   */
  def getInt(key: String, defaultValue: Int): Int = catchIllegalValue(key) {
    getOption(key).map(_.toInt).getOrElse(defaultValue)
  }

  /**
   * Get a parameter as a long, falling back to a default if not set
   *
   * @throws NumberFormatException If the value cannot be interpreted as a long
   */
  def getLong(key: String, defaultValue: Long): Long = catchIllegalValue(key) {
    getOption(key).map(_.toLong).getOrElse(defaultValue)
  }

  /**
   * Get a parameter as a double, falling back to a default if not ste
   *
   * @throws NumberFormatException If the value cannot be interpreted as a double
   */
  def getDouble(key: String, defaultValue: Double): Double = catchIllegalValue(key) {
    getOption(key).map(_.toDouble).getOrElse(defaultValue)
  }

  /**
   * Get a parameter as a boolean, falling back to a default if not set
   *
   * @throws IllegalArgumentException If the value cannot be interpreted as a boolean
   */
  def getBoolean(key: String, defaultValue: Boolean): Boolean = catchIllegalValue(key) {
    getOption(key).map(_.toBoolean).getOrElse(defaultValue)
  }

  def contains(key: String): Boolean = {
    settings.containsKey(key)
  }

  override def clone: AbstractConf = {
    val cloned = new AbstractConf(prefix, false)
    settings.entrySet().asScala.foreach { e =>
      cloned.set(e.getKey(), e.getValue())
    }
    cloned
  }

  private[config] def contains(entry: ConfigEntry[_]): Boolean = contains(entry.key)

  private def catchIllegalValue[T](key: String)(getValue: => T): T = {
    try {
      getValue
    } catch {
      case e: NumberFormatException =>
        // NumberFormatException doesn't have a constructor that takes a cause for some reason.
        throw new NumberFormatException(s"Illegal value for config key $key: ${e.getMessage}")
          .initCause(e)
      case e: IllegalArgumentException =>
        throw new IllegalArgumentException(s"Illegal value for config key $key: ${e.getMessage}", e)
    }
  }
}
