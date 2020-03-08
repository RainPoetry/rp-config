package rainpoetry.config.scala.core.providers

import rainpoetry.config.scala.core.entries._

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description: 
 */

class AbstractConfProvider(prefix: String, conf: java.util.Map[String, String]) extends ConfigProvider {

  // 读取带有 prefix 前缀的属性
  override def get(key: String): Option[String] = {
    if (key.startsWith(prefix)) {
      Option(conf.get(key)).orElse(defaultValueString(key))
    } else {
      None
    }
  }

  private def defaultValueString(key: String): Option[String] = {
    ConfigEntry.findEntry(key) match {
      case e: ConfigEntryWithDefault[_] => Option(e.defaultValueString)
      case e: ConfigEntryWithDefaultString[_] => Option(e.defaultValueString)
      case e: ConfigEntryWithDefaultFunction[_] => Option(e.defaultValueString)
      case e: FallbackConfigEntry[_] => defaultValueString(e.fallback.key)
      case _ => None
    }

  }


}
