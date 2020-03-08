package rainpoetry.config.scala

import java.util.concurrent.TimeUnit

import rainpoetry.config.scala.core.ConfigBuilder

/**
 *
 * @author RainPoetry
 */

package object test {

  val STRING = ConfigBuilder("rp.string")
    .withAlternative("")
    .withAlternative("")
    .stringConf
    .createWithDefaultString("abc")

  val INT = ConfigBuilder("rp.int")
    .intConf
    .createWithDefault(2)


  val FUNC = ConfigBuilder("rp.func")
    .timeConf(TimeUnit.SECONDS)
    .createWithDefaultFunction(() => 100 + 100)

  val OPTION = ConfigBuilder("rp.option")
    .withAlternative("")
    .timeConf(TimeUnit.SECONDS)
    .createOptional

  val NO_PREFIX = ConfigBuilder("noprefix")
    .withAlternative("rp.string")
    .stringConf
    .createWithDefaultString("bbb")

  val ALTERNATIVE = ConfigBuilder("rp.alternative")
    .withAlternative("rp.string")
    .stringConf
    .createOptional

}
