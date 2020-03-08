package rainpoetry.config.scala.tools

import java.util.Locale

import java.lang.{Long => JLong}

import scala.collection.immutable

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description: 
 */

object ByteUnits {

  val pattern = "(-?[0-9]+)([a-z]+)?".r

  val byteSuffixes = immutable.HashMap[String, ByteUnit](
    "b" -> ByteUnit.BYTE,
    "k" -> ByteUnit.KiB,
    "kb" -> ByteUnit.KiB,
    "m" -> ByteUnit.MiB,
    "mb" -> ByteUnit.MiB,
    "g" -> ByteUnit.GiB,
    "gb" -> ByteUnit.GiB,
    "t" -> ByteUnit.TiB,
    "tb" -> ByteUnit.TiB,
    "p" -> ByteUnit.PiB,
    "pb" -> ByteUnit.PiB
  )

  def byteStringAs(str: String, unit: ByteUnit): Long = {
    val lower = str.toLowerCase(Locale.ROOT).trim
    lower match {
      case pattern(number, suffix) =>
        val value = JLong.parseLong(number)
        if (suffix != null && !byteSuffixes.contains(suffix)) {
          throw new NumberFormatException("Invalid suffix: \"" + suffix + "\"");
        }
        if (suffix != null) {
          unit.convertFrom(value, byteSuffixes(suffix))
        } else {
          unit.convertFrom(value, unit)
        }
      case _ => throw new NumberFormatException(s"Failed to parse time string: ${str}")
    }
  }

  def byteStringAsBytes(str: String): Long = {
    byteStringAs(str, ByteUnit.BYTE)
  }

  def byteStringAsKb(str: String): Long = {
    byteStringAs(str, ByteUnit.KiB)
  }

  def byteStringAsMb(str: String): Long = {
    byteStringAs(str, ByteUnit.MiB)
  }

  def byteStringAsGb(str: String): Long = {
    byteStringAs(str, ByteUnit.GiB)
  }
}
