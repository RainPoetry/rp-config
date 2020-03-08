package rainpoetry.config.scala.core.providers

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description: 
 */

class MapProvider(conf: java.util.Map[String, String]) extends ConfigProvider {
  override def get(key: String): Option[String] = Option(conf.get(key))
}
