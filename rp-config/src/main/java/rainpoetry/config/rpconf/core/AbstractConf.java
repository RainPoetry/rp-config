package rainpoetry.config.rpconf.core;


import rainpoetry.config.rpconf.annotation.RpConf;
import rainpoetry.config.rpconf.common.TimeUnis;

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

	private final Map<String, Object> options = new HashMap<>();

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

				// 允许访问 private
				field.setAccessible(true);
				Object value = field.get(cls);

				// 重复 key 检测
				if (options.containsKey(rpConf.value())) {
					throw new RuntimeException("exist repeat key " + rpConf.value() + "  in class " + cls.getSimpleName());
				}
				switch (rpConf.type()) {
					case TIMEUNIT:
					case BYTEUNIT:
						if (fieldClass != String.class) {
							throw new RuntimeException("expect field " + field.getName() + " be String in class " + cls.getSimpleName());
						}
						break;
				}
				options.put(rpConf.value(), value);
				confs.put(rpConf.value(), new MetaInfo(fieldClass, rpConf));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		origins = Collections.unmodifiableMap(options);
		confMeta = Collections.unmodifiableMap(confs);
		this.options.putAll(options);
	}

	public AbstractConf set(String key, Object value) {
		Object v = check(key, value);
		options.put(key, v);
		return this;
	}

	public void setAll(Map<String, Object> maps) {
		maps.forEach((k, v) -> set(k, v));
	}

	public Object get(String key) {
		valid();
		if (options.containsKey(key)) {
			return options.get(key);
		}
		throw new RuntimeException(key + " not exist");
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

	private Object check(String key, Object value) {
		MetaInfo conf = confMeta.get(key);
		if (conf == null) {
			throw new RuntimeException(" key is not exists : " + key);
		}
		Class cls = conf.getDataType();
		return parseType(key, cls, value);
	}

	public void valid() {
		if (!ischeck.get()) {
			confMeta.forEach((k, v) -> {
				if (v.getConf().need() && (origins.get(k) == null || options.get(k) == null)) {
					throw new RuntimeException(" key is not allow null : " + k);
				}
			});
			ischeck.compareAndSet(false, true);
		}
	}

	private Object parseType(String key, Class c, Object value) {
		if (c == Integer.TYPE || c == Integer.class) {
			if (value instanceof Integer) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.matches("(-)?\\d+")) {
					return Integer.parseInt(str);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to int ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Long.TYPE || c == Long.class) {
			if (value instanceof Long) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.matches("(-)?\\d+")) {
					return Long.parseLong(str);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to Long ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Double.TYPE || c == Double.class) {
			if (value instanceof Double) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.matches("(-)?\\d+(\\.\\d+)?")) {
					return Double.parseDouble(str);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to double ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Float.TYPE || c == Float.class) {
			if (value instanceof Float) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.matches("(-)?\\d+(\\.\\d+)?")) {
					return Float.parseFloat(str);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to float ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Boolean.TYPE || c == Boolean.class) {
			if (value instanceof Boolean) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.matches("(true|false)")) {
					return Boolean.parseBoolean(str);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to boolean ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Character.TYPE || c == Character.class) {
			if (value instanceof Character) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.length() == 1) {
					return str.charAt(0);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to char ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Byte.TYPE || c == Byte.class) {
			if (value instanceof Byte) {
				return value;
			}
			throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
		} else if (c == Short.TYPE || c == Short.class) {
			if (value instanceof Short) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				if (str.matches("(-)?\\d+")) {
					return Short.parseShort(str);
				}
				throw new RuntimeException(key + " config error: " + str + " can not convert to short ");
			} else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else if (c == Class.class) {
			if (value instanceof Class) {
				return value;
			} else if (value instanceof String) {
				String str = ((String) value).trim();
				try {
					return Class.forName(str);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(key + " config error: " + e.getMessage());
				}
			}else {
				throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
			}
		} else {
			// c 是否是 value 的父类
			if (c.isAssignableFrom(value.getClass())) {
				return c;
			}
			throw new RuntimeException(key + " config error: expect type is " + c + " but is " + value.getClass());
		}
	}

	public void print() {
		System.out.println(options);
	}

}
