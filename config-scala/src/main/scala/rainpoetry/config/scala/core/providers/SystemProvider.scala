package rainpoetry.config.scala.core.providers

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description: 
 */

class SystemProvider extends ConfigProvider {
  override def get(key: String): Option[String] = sys.props.get(key)
}
