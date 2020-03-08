package rainpoetry.config.scala.core

import java.util.{Map => JMap}

import rainpoetry.config.scala.core.entries._
import rainpoetry.config.scala.core.providers.{ConfigProvider, EnvProvider, MapProvider, SystemProvider}

import scala.collection.mutable
import scala.util.matching.Regex

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description:
 *
 *    用于读取 Config
 */

class ConfigReader(conf: ConfigProvider) {

  def this(conf: java.util.Map[String, String]) = this(new MapProvider(conf))

  private val bindings = new mutable.HashMap[String, ConfigProvider]()

  bind(null, conf)
  bindEnv(new EnvProvider())
  bindSystem(new SystemProvider())

  def bind(prefix: String, provider: ConfigProvider): ConfigReader = {
    bindings(prefix) = provider
    this
  }

  def bind(prefix: String, values: java.util.Map[String, String]): ConfigReader = {
    bind(prefix, new MapProvider(values))
  }

  def bindEnv(provider: ConfigProvider): ConfigReader = bind("env", provider)

  def bindSystem(provider: ConfigProvider): ConfigReader = bind("system", provider)

  def get(key: String): Option[String] = conf.get(key)

  def substitute(input: String): String = substitute(input, Set())

  private def substitute(input: String, usedRefs: Set[String]): String = {
    if (input != null) {
      ConfigReader.REF_RE.replaceAllIn(input, { m =>
        val prefix = m.group(1)
        val name = m.group(2)
        val ref = if (prefix == null) name else s"$prefix:$name"
        require(!usedRefs.contains(ref), s"Circular reference in $input: $ref")

        val replacement = bindings.get(prefix)
          .flatMap(getOrDefault(_, name))
          .map { v => substitute(v, usedRefs + ref) }
          .getOrElse(m.matched)

        println(replacement)
        Regex.quoteReplacement(replacement)
      })
    } else {
      input
    }
  }

  private def getOrDefault(conf: ConfigProvider, key: String): Option[String] = {
    conf.get(key).orElse {
      ConfigEntry.findEntry(key) match {
        case e: ConfigEntryWithDefault[_] => Option(e.defaultValueString)
        case e: ConfigEntryWithDefaultString[_] => Option(e.defaultValueString)
        case e: ConfigEntryWithDefaultFunction[_] => Option(e.defaultValueString)
        case e: FallbackConfigEntry[_] => getOrDefault(conf, e.fallback.key)
        case _ => None
      }
    }
  }
}


object ConfigReader {
  private val REF_RE = "\\$\\{(?:(\\w+?):)?(\\S+?)\\}".r
}
