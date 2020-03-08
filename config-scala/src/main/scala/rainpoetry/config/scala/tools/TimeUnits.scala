package rainpoetry.config.scala.tools

import java.util.Locale
import java.util.concurrent.TimeUnit

import scala.collection.immutable
import java.lang.{Long => JLong}


/*
 * author:  RainPoetry
 * date:  2019/8/21
 * description: 
 */

object TimeUnits {

  val pattern = "(-?[0-9]+)([a-z]+)?".r

  val timeSuffix = immutable.HashMap[String, TimeUnit](
    "us" -> TimeUnit.MICROSECONDS,
    "ms" -> TimeUnit.MILLISECONDS,
    "s" -> TimeUnit.SECONDS,
    "m" -> TimeUnit.MINUTES,
    "min" -> TimeUnit.MINUTES,
    "h" -> TimeUnit.HOURS,
    "d" -> TimeUnit.DAYS
  )

  def timeStringAs(str: String, unit: TimeUnit): Long = {
    val lower = str.toLowerCase(Locale.ROOT).trim
    lower match {
      case pattern(number, suffix) =>
        val value = JLong.parseLong(number)
        if (suffix != null && !timeSuffix.contains(suffix)) {
          throw new NumberFormatException("Invalid suffix: \"" + suffix + "\"");
        }
        if (suffix != null) {
          unit.convert(value, timeSuffix(suffix))
        } else {
          unit.convert(value, unit)
        }
      case _ => throw new NumberFormatException(s"Failed to parse time string: ${str}")
    }
  }

  def timeStringAsMs(str: String): Long = {
    timeStringAs(str, TimeUnit.MILLISECONDS)
  }

  def timeStringAsSec(str: String): Long = {
    timeStringAs(str, TimeUnit.SECONDS)
  }

}
