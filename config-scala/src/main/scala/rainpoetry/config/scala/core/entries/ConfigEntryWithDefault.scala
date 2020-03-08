package rainpoetry.config.scala.core.entries

import rainpoetry.config.scala.core.ConfigReader

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    带有默认值的 Config
 *
 *     如果 key 没有配置，那么使用默认值
 */

class ConfigEntryWithDefault[T](key: String,
                                alternatives: List[String],
                                _defaultValue: T,
                                valueConverter: String => T,
                                stringConverter: T => String,
                                doc: String) extends ConfigEntry(key, alternatives, valueConverter, stringConverter, doc) {

  override def defaultValue: Option[T] = Option(_defaultValue)

  override def defaultValueString: String = stringConverter(_defaultValue)

  override def readFrom(reader: ConfigReader): T = {
    reader.get(key).map(valueConverter).getOrElse(_defaultValue)
  }
}
