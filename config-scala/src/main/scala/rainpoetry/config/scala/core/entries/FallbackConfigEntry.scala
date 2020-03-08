package rainpoetry.config.scala.core.entries

import rainpoetry.config.scala.core.ConfigReader

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    默认值被其他的 Config 决定
 */

class FallbackConfigEntry[T] (
                               key: String,
                               alternatives: List[String],
                               doc: String,
                               val fallback: ConfigEntry[T])
  extends ConfigEntry[T](key, alternatives,
    fallback.valueConverter, fallback.stringConverter, doc) {

  override def defaultValueString: String = s"<value of ${fallback.key}>"

  override def readFrom(reader: ConfigReader): T = {
    readString(reader).map(valueConverter).getOrElse(fallback.readFrom(reader))
  }
}
