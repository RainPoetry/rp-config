package rainpoetry.config.rpconf;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
		assertEquals(conf.get("rp.string"), "a");
		assertEquals(conf.get("rp.int"), 0);
	}

	@Test
	public void setValue() {
		conf.set("rp.int",999);
		assertEquals(conf.get("rp.int"), 999);
	}
}
