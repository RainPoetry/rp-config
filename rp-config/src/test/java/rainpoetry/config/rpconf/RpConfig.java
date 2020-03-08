package rainpoetry.config.rpconf;

import rainpoetry.config.rpconf.annotation.RpConf;
import rainpoetry.config.rpconf.core.AbstractConf;

/**
 * @author RainPoetry
 */

public class RpConfig extends AbstractConf {

	@RpConf("rp.string")
	private static final String A = "a";

	@RpConf("rp.int")
	private static int B;


	public RpConfig() {
		super(RpConfig.class);
	}
}
