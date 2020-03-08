package rainpoetry.config.commconf;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author RainPoetry
 */

public class RpConfigTest {

	private RpConfig conf;

	@Before
	public void before() {
		conf = new RpConfig();
	}

	@Test
	public void getValue() {
		assertEquals(conf.getString("rp.string"), "a");
		assertEquals(conf.getInt("rp.int"), 2);

		assertEquals(conf.getString(RpConfig.STRING), "a");
		assertEquals(conf.getInt(RpConfig.INT), 2);
	}

	@Test
	public void setValue() {
		conf.set("rp.int", "999");
		assertEquals(conf.getInt("rp.int"), 999);
	}
}
