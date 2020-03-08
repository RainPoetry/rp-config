package rainpoetry.config.commconf.core;


import rainpoetry.config.commconf.annotation.RpConf;
import rainpoetry.config.commconf.common.ByteUnits;
import rainpoetry.config.commconf.common.TimeUnis;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author RainPoetry
 */

public class AbstractConf {

	private final Map<String, Object> origins;
	private final Map<String, MetaInfo> confMeta;

	private final Map<String, Object> settings = new HashMap<>();

	private final AtomicBoolean ischeck = new AtomicBoolean(false);

	public AbstractConf(Class cls) {
		Map<String, Object> options = new HashMap<>();
		Map<String, MetaInfo> confs = new HashMap<>();
		try {
			for (Field field : cls.getDeclaredFields()) {
				RpConf rpConf = field.getDeclaredAnnotation(RpConf.class);
				if (rpConf == null) {
					continue;
				}
				// static 检测
				if (!Modifier.isStatic(field.getModifiers())) {
					throw new RuntimeException(field.getName() + " must be static in class " + cls.getSimpleName());
				}
				Class fieldClass = field.getType();
				if (fieldClass != String.class) {
					throw new RuntimeException(field.getName() + " must be String in class " + cls.getSimpleName());
				}
				// 允许访问 private
				field.setAccessible(true);
				String key = (String) field.get(cls);

				// 重复 key 检测
				if (options.containsKey(rpConf.value())) {
					throw new RuntimeException("exist repeat key " + rpConf.value() + "  in class " + cls.getSimpleName());
				}
				if (rpConf.value().isEmpty()) {
					options.put(key, null);
				} else {
					options.put(key, parseType(key, rpConf.type(), rpConf.value()));
				}
				confs.put(key, new MetaInfo(fieldClass, rpConf));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		origins = Collections.unmodifiableMap(options);
		confMeta = Collections.unmodifiableMap(confs);
		this.settings.putAll(options);
	}

	public AbstractConf set(String key, String value) {
		Object v = check(key, value);
		settings.put(key, v);
		return this;
	}

	public void setAll(Map<String, String> maps) {
		maps.forEach((k, v) -> set(k, v));
	}

	public Object get(String key) {
		valid();
		if (settings.containsKey(key)) {
			Object v = settings.get(key);
			if (v == null) {
				throw new IllegalStateException("key: \'" + key + "\' is null , user need to config it.");
			}
			return v;
		}
		throw new RuntimeException("key: \'" +key + "\' not exist");
	}

	public String getString(String key) {
		return (String) get(key);
	}

	public int getInt(String key) {
		return (int) get(key);
	}

	public long getLong(String key) {
		return (long) get(key);
	}

	public double getDouble(String key) {
		return (double) get(key);
	}

	public float getFloat(String key) {
		return (float) get(key);
	}

	public char getChar(String key) {
		return (char) get(key);
	}

	public boolean getBoolean(String key) {
		return (boolean) get(key);
	}

	public Class getClass(String key) {
		return (Class) get(key);
	}

	public long getTimeAsSec(String key) {
		return TimeUnis.timeStringAsSec(getString(key));
	}

	public long getTimeAsMs(String key) {
		return TimeUnis.timeStringAsMs(getString(key));
	}

	private Object check(String key, String value) {
		MetaInfo conf = confMeta.get(key);
		if (conf == null) {
			throw new RuntimeException(" key is not exists : " + key);
		}
		Class cls = conf.getDataType();
		return parseType(key, conf.getConf().type(), value);
	}

	public void valid() {
		if (!ischeck.get()) {
			confMeta.forEach((k, v) -> {
				if (v.getConf().need() && settings.get(k) == null) {
					throw new RuntimeException(" key is not allow null : " + k);
				}
			});
			ischeck.compareAndSet(false, true);
		}
	}

	private Object parseType(String key, Types type, String value) {
		switch (type) {
			case STRING:
				return value;
			case INT:
				value = value.trim();
				if (value.matches("(-)?\\d+")) {
					return Integer.parseInt(value);
				}
				throw new RuntimeException(key + " config error: " + value + " can not convert to int ");
			case LONG:
				value = value.trim();
				if (value.matches("(-)?\\d+")) {
					return Long.parseLong(value);
				}
				throw new RuntimeException(key + " config error: " + value + " can not convert to Long ");
			case DOUBLE:
				value = value.trim();
				if (value.matches("(-)?\\d+(\\.\\d+)?")) {
					return Double.parseDouble(value);
				}
				throw new RuntimeException(key + " config error: " + value + " can not convert to double ");
			case FLOAT:
				value = value.trim();
				if (value.matches("(-)?\\d+(\\.\\d+)?")) {
					return Float.parseFloat(value);
				}
				throw new RuntimeException(key + " config error: " + value + " can not convert to float ");
			case BOOLEAN:
				value = value.trim();
				if (value.matches("(true|false)")) {
					return Boolean.parseBoolean(value);
				}
				throw new RuntimeException(key + " config error: " + value + " can not convert to boolean ");
			case SHORT:
				value = value.trim();
				if (value.matches("(-)?\\d+")) {
					return Short.parseShort(value);
				}
				throw new RuntimeException(key + " config error: " + value + " can not convert to short ");
			case CLASS:
				value = value.trim();
				try {
					return Class.forName(value);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(key + " config error: " + e.getMessage());
				}
			case TIME:
				value = value.trim();
				if (TimeUnis.match(value)) {
					return value;
				}
				throw new RuntimeException(key + " Time format error: " + value);
			case BYTES:
				value = value.trim();
				if (ByteUnits.match(value)) {
					return value;
				}
				throw new RuntimeException(key + " Bytes format error: " + value);
			default:
				throw new RuntimeException("unreachable");
		}
	}


	public void print() {
		System.out.println(settings);
	}

}
