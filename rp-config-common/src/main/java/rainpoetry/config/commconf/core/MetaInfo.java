package rainpoetry.config.commconf.core;

import rainpoetry.config.commconf.annotation.RpConf;

/**
 * @author RainPoetry
 */

public class MetaInfo {

	// 数据的类型
	private final Class dataType;
	// Types 类型
	private final RpConf conf;

	public MetaInfo(Class dataType, RpConf conf) {
		this.dataType = dataType;
		this.conf = conf;
	}

	public Class getDataType() {
		return dataType;
	}

	public RpConf getConf() {
		return conf;
	}
}
