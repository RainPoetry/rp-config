package rainpoetry.config.scala.core.entries

import rainpoetry.config.scala.core.ConfigReader

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    带有默认字符串的 Config
 *
 *    如果默认值带有 ${} 修饰
 */

class ConfigEntryWithDefaultString[T](key: String,
                                      alternatives: List[String],
                                      _defaultValue: String,
                                      valueConverter: String => T,
                                      stringConverter: T => String,
                                      doc: String) extends ConfigEntry(key, alternatives, valueConverter, stringConverter, doc) {

  override def defaultValue: Option[T] = Some(valueConverter(_defaultValue))

  override def defaultValueString: String = _defaultValue

  override def readFrom(reader: ConfigReader): T = {
    val value = readString(reader).getOrElse(reader.substitute(_defaultValue))
    valueConverter(value)
  }
}

