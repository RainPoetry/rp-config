package rainpoetry.config.scala.core.providers

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description: 
 */

class EnvProvider extends ConfigProvider {
  override def get(key: String): Option[String] = sys.env.get(key)
}
