package rainpoetry.config.kafka.validate;


import rainpoetry.config.kafka.ConfigException;

/**
 * User: chenchong
 * Date: 2018/11/16
 * description:
 */
public class NonNullValidator implements Validator {
	@Override
	public void ensureValid(String name, Object value) {
		if (value == null) {
			// Pass in the string null to avoid the spotbugs warning
			throw new ConfigException(name, "null", "entry must be non null");
		}
	}

	@Override
	public String toString() {
		return "non-null string";
	}
}
