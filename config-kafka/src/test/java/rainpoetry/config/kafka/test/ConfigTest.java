package rainpoetry.config.kafka.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import rainpoetry.config.kafka.ConfigException;

import static org.junit.Assert.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RainPoetry
 */

public class ConfigTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void defaultValue() {
		RPConfig conf = new RPConfig(Collections.EMPTY_MAP);
		assertEquals(conf.getString(RPConfig.USER), "admin");
		assertEquals(conf.getInt(RPConfig.AGE), 60);
	}

	@Test
	public void validateTest() {
		exception.expect(ConfigException.class);
		exception.expectMessage("String must be non-empty");
		Map<String, Object> maps = new HashMap<>();
		maps.put(RPConfig.USER, "");
		RPConfig conf = new RPConfig(maps);
	}


}
