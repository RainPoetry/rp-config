package rainpoetry.config.scala.core

import rainpoetry.config.scala.core.entries._

/*
 * author:  RainPoetry
 * date:  2019/8/23
 * description: 
 */

class TypedConfigBuilder[T](val parent: ConfigBuilder,
                            val converter: String => T,
                            val stringConverter: T => String) {

  import ConfigHelpers._

  def this(parent: ConfigBuilder, converter: String => T) = {
    this(parent, converter, Option(_).map(_.toString).orNull)
  }

  /** Apply a transformation to the user-provided values of the config entry. */
  def transform(fn: T => T): TypedConfigBuilder[T] = {
    new TypedConfigBuilder(parent, s => fn(converter(s)), stringConverter)
  }

  /** Checks if the user-provided value for the config matches the validator. */
  def checkValue(validator: T => Boolean, errorMsg: String): TypedConfigBuilder[T] = {
    transform { v =>
      if (!validator(v)) throw new IllegalArgumentException(errorMsg)
      v
    }
  }

  /** Check that user-provided values for the config match a pre-defined set. */
  def checkValues(validValues: Set[T]): TypedConfigBuilder[T] = {
    transform { v =>
      if (!validValues.contains(v)) {
        throw new IllegalArgumentException(
          s"The value of ${parent.key} should be one of ${validValues.mkString(", ")}, but was $v")
      }
      v
    }
  }

  /** Turns the config entry into a sequence of values of the underlying type. */
  def toSequence: TypedConfigBuilder[Seq[T]] = {
    new TypedConfigBuilder[Seq[T]](parent, stringToSeq(_, converter), seqToString(_, stringConverter))
  }

  /** Creates a [[ConfigEntry]] that does not have a default value. */
  def createOptional: OptionalConfigEntry[T] = {
    val entry = new OptionalConfigEntry[T](parent.key, parent._alternatives, converter,
      stringConverter, parent._doc)
    parent._onCreate.foreach(_ (entry))
    entry
  }

  /** Creates a [[ConfigEntry]] that has a default value. */
  def createWithDefault(default: T): ConfigEntry[T] = {
    // Treat "String" as a special case, so that both createWithDefault and createWithDefaultString
    // behave the same w.r.t. variable expansion of default values.
    if (default.isInstanceOf[String]) {
      createWithDefaultString(default.asInstanceOf[String])
    } else {
      val transformedDefault = converter(stringConverter(default))
      val entry = new ConfigEntryWithDefault[T](parent.key, parent._alternatives,
        transformedDefault, converter, stringConverter, parent._doc)
      parent._onCreate.foreach(_ (entry))
      entry
    }
  }

  /** Creates a [[ConfigEntry]] with a function to determine the default value */
  def createWithDefaultFunction(defaultFunc: () => T): ConfigEntry[T] = {
    val entry = new ConfigEntryWithDefaultFunction[T](parent.key, parent._alternatives, defaultFunc,
      converter, stringConverter, parent._doc)
    parent._onCreate.foreach(_ (entry))
    entry
  }

  /**
   * Creates a [[ConfigEntry]] that has a default value. The default value is provided as a
   * [[String]] and must be a valid value for the entry.
   *
   */
  def createWithDefaultString(default: String): ConfigEntry[T] = {
    val entry = new ConfigEntryWithDefaultString[T](parent.key, parent._alternatives, default,
      converter, stringConverter, parent._doc)
    parent._onCreate.foreach(_ (entry))
    entry
  }

}
