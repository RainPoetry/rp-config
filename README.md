# Config

## 使用推荐指南

> config-kafka

- 需要对配置参数值进行严格的校验的场景
- 配置不够精炼

> config-scala

- 除了配置参数定义比较繁琐外，没什么缺点

> rp-config

- 配置简单
- 配置值可以是任意的数据类型
- 读取配置的时候，只能通过硬编码 key 的方式来进行

> rp-config-common

相当于是 rp-config 缺点进行规避的一个版本

- 配置简单
- 配置值都必须是字符串

## config-kafka

### 样例

> Config 配置

```java
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
```

> 使用方式

```java
RPConfig conf = new RPConfig(Collections.EMPTY_MAP);
conf.getString(RPConfig.USER)
```

## config-scala

### 样例

> Config 配置

```scala
class RPConf extends AbstractConf("rp") {}

val STRING = ConfigBuilder("rp.string")
	.withAlternative("")
    .withAlternative("")
    .stringConf
    .createWithDefaultString("abc")
```

> 使用方式

```scala
val conf = new RPConf()
conf.get(STRING)
```

### AbstractConf 

带有 Config 的 set 和 get 默认实现，所有的 Config 实现都需要继承这个抽象类

> 参数接口

- `prefix`：只有带有 prefix 前缀的参数，才能被修改，用于实现一些配置参数的安全保护
- `loadDefaults`：用于从系统变量中加载配置信息

### ConfigEntryWithDefaultString

带有默认值的 Config ，值只允许为字符串

```
  val a = ConfigBuilder("rp.string")
    .withAlternative("key2")
    .stringConf
    .createWithDefaultString("default")
```

> 读取策略

- 先读取用户配置值（需要配置的 `key` 带有 `prefix` 前缀）
- 没有则使用默认值
- 再没有则读取 `withAlternative` 里面的 `key2` 对应的值
  - 先读取用户配置值（需要配置的 `key2` 带有 prefix 前缀）
  - 再读取  `key2`  的默认值

> 注意

- 默认值支持 `${key3}` 的形式，读取 `key3` 对应的值，按照 `读取策略` 流程来获取
- `withAlternative` 可以有多个

### ConfigEntryWithDefault

带有默认值的 Config，值允许为任何数据

```
 val DEFAULT_INT = ConfigBuilder("rp.int")
    .intConf
    .createWithDefault(2)
```

> 读取策略

- 先读取用户配置值（需要配置的 `key` 带有 `prefix` 前缀）
- 最后使用默认值

> 注意

- 默认值不支持 `${key3}`  
- 当数据类型是 `string` 的时候，`ConfigEntryWithDefault` 会自动转换为 `ConfigEntryWithDefaultString`
- 不支持 `withAlternative`

### ConfigEntryWithDefaultFunction

支持带有 scala 代码的 config

```
  val FUNC = ConfigBuilder("rp.func")
    .timeConf(TimeUnit.SECONDS)
    .createWithDefaultFunction(() => 100 + 100)
```

> 读取策略

- 先读取用户配置值（需要配置的 `key` 带有 `prefix` 前缀）
- 最后使用默认值

### OptionalConfigEntry

没有默认值的 config

```
  val OPTION = ConfigBuilder("rp.option")
    .withAlternative("key2")
    .timeConf(TimeUnit.SECONDS)
    .createOptional
```

> 读取策略

- 先读取用户配置值（需要配置的 `key` 带有 `prefix` 前缀）

- 再读取 `withAlternative` 里面的 `key2` 对应的值
  - 读取用户配置值（需要配置的 `key2` 带有 prefix 前缀）
  - 再读取  `key2`  的默认值

### 设置和读取数据方式

> 设置数据

- 直接调用 set 方法，通过 对应的 key 来上设置

> 取数据

- get 参数是 String
  - 直接从 用户配置参数中区
  - 不会读取默认值
- get 参数是 ConfigEntry
  - 参考上面的 4 个读取策略

## rp-config

### 样例

> Config 配置

```java
public class RpConfig extends AbstractConf {

	@RpConf("rp.string")
	private static final String A = "a";

	@RpConf("rp.int")
	private static int B;


	public RpConfig() {
		super(RpConfig.class);
	}
}
```

> 使用方式

```java
RpConfig conf = new RpConfig();
conf.get("rp.string");
```

## rp-config-common

### 样例

> config 配置

```java
public class RpConfig extends AbstractConf {

	@RpConf(value = "a", type = Types.STRING)
	public static final String STRING = "rp.string";

	@RpConf(value = "", type = Types.INT)
	public static final String INT = "rp.int";

	public RpConfig() {
		super(RpConfig.class);
	}
}
```

> 使用方式

```java
RpConfig conf = new RpConfig();
conf.get("rp.string");
conf.get(RpConfig.STRING);
```

