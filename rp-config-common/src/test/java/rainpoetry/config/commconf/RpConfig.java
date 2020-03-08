package rainpoetry.config.commconf;

import rainpoetry.config.commconf.annotation.RpConf;
import rainpoetry.config.commconf.core.AbstractConf;
import rainpoetry.config.commconf.core.Types;

/**
 * @author RainPoetry
 */

public class RpConfig extends AbstractConf {

	@RpConf(value = "a", type = Types.STRING)
	public static final String STRING = "rp.string";

	@RpConf(value = "", type = Types.INT)
	public static final String INT = "rp.int";

	public RpConfig() {
		super(RpConfig.class);
	}
}
