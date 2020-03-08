package rainpoetry.config.scala.test

import java.util.concurrent.TimeUnit

import org.scalatest.FunSuite
import rainpoetry.config.scala.core.ConfigBuilder

/**
 *
 * @author RainPoetry
 */

class ConfigSuit extends FunSuite {
  val conf = new RPConf()

  test("读取配置") {
    assertResult(conf.get(STRING))("abc")
    assertResult(conf.get(INT))(2)
    assertResult(conf.get(FUNC))(200)
  }

  test("用户设置参数") {
    conf.set("rp.string","12345")
    assertResult(conf.get(STRING))("12345")
  }

  test("没有 prefix 的参数无法设置") {
    conf.set("noprefix","12345")
    assertResult(conf.get(NO_PREFIX))("a")
  }

  test("alternative 测试") {
    assertResult(conf.get(ALTERNATIVE).get)(conf.get(STRING))
  }

}
