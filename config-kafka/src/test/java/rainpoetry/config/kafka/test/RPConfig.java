package rainpoetry.config.kafka.test;

import rainpoetry.config.kafka.AbstractConfig;
import rainpoetry.config.kafka.ConfigDef;
import rainpoetry.config.kafka.validate.NonEmptyString;
import rainpoetry.config.kafka.validate.Range;

import java.util.Map;

/**
 * @author RainPoetry
 */

public class RPConfig extends AbstractConfig {

	private static final ConfigDef definition;

	public static final String USER = "user";
	public static final String USER_DEFAULT = "admin";

	public static final String AGE = "age";
	public static final int AGE_DEFAULT = 60;

	static {
		definition = new ConfigDef()
				.define(USER, ConfigDef.Type.STRING, USER_DEFAULT, new NonEmptyString())
				.define(AGE, ConfigDef.Type.INT, AGE_DEFAULT, Range.between(0, 100));
	}

	public RPConfig(Map<?, ?> originals) {
		super(definition, originals);
	}
}
