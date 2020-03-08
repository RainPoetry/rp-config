package rainpoetry.config.scala.core.entries

import rainpoetry.config.scala.core.ConfigReader

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    带有默认函数的 Config
 *
 */

class ConfigEntryWithDefaultFunction[T](
                                         key: String,
                                         alternatives: List[String],
                                         _defaultFunction: () => T,
                                         valueConverter: String => T,
                                         stringConverter: T => String,
                                         doc: String) extends ConfigEntry(key, alternatives, valueConverter, stringConverter, doc) {

  override def defaultValue: Option[T] = Some(_defaultFunction())

  override def defaultValueString: String = stringConverter(_defaultFunction())

  override def readFrom(reader: ConfigReader): T = {
    reader.get(key).map(valueConverter).getOrElse(_defaultFunction())
  }
}
