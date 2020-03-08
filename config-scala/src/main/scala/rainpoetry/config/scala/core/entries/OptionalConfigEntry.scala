package rainpoetry.config.scala.core.entries

import rainpoetry.config.scala.core.ConfigReader

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *      没有默认值的 Config
 *
 */

class OptionalConfigEntry[T](
                              key: String,
                              alternatives: List[String],
                              val rawValueConverter: String => T,
                              val rawStringConverter: T => String,
                              doc: String)
  extends ConfigEntry[Option[T]](key, alternatives,
    s => Some(rawValueConverter(s)),
    v => v.map(rawStringConverter).orNull, doc) {

  override def defaultValueString: String = ConfigEntry.UNDEFINED

  override def readFrom(reader: ConfigReader): Option[T] = {
    readString(reader).map(rawValueConverter)
  }
}