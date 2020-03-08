package rainpoetry.config.scala.core.providers

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    Config 的存储方式：
 *      map、系统环境变量、系统变量
 */

trait ConfigProvider {
  def get(key: String): Option[String]
}
