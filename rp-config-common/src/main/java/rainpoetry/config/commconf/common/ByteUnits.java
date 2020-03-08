package rainpoetry.config.commconf.common;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author RainPoetry
 */

public class ByteUnits {

	private static final String pattern = "(-?[0-9]+)([a-z]+)?";

	private static final Map<String, ByteUnit> byteSuffixes;

	static {
		byteSuffixes = Maps.newHashMap()
				.pairs("b", ByteUnit.BYTE)
				.pairs("k", ByteUnit.KiB)
				.pairs("kb", ByteUnit.KiB)
				.pairs("m", ByteUnit.MiB)
				.pairs("mb", ByteUnit.MiB)
				.pairs("g", ByteUnit.GiB)
				.pairs("gb", ByteUnit.GiB)
				.pairs("t", ByteUnit.TiB)
				.pairs("tb", ByteUnit.TiB)
				.pairs("p", ByteUnit.PiB)
				.pairs("pb", ByteUnit.PiB)
				.unModify();
	}

	public static long byteStringAS(String format, ByteUnit unit) {
		String lower = format.toLowerCase().trim();
		if (match(lower)) {
			Matcher matcher = Pattern.compile(pattern).matcher(lower);
			if (matcher.find()) {
				String suffix = matcher.group(2);
				long number = Long.parseLong(matcher.group(1));
				if (suffix != null) {
					return unit.convertFrom(number, byteSuffixes.get(suffix));
				} else {
					return unit.convertFrom(number, unit);
				}
			}
		}
		throw new NumberFormatException("Failed to parse time string: " + format);
	}

	public static boolean match(String format) {
		return format.matches(pattern);
	}

	public static long byteStringAsBytes(String str) {
		return byteStringAS(str, ByteUnit.BYTE);
	}

	public static long byteStringAsKb(String str) {
		return byteStringAS(str, ByteUnit.KiB);
	}

	public static long byteStringAsMb(String str){
		return byteStringAS(str, ByteUnit.MiB);
	}

	public static long byteStringAsGb(String str){
		return byteStringAS(str, ByteUnit.GiB);
	}

	public static void main(String[] args) {
		System.out.println(byteStringAS("2M", ByteUnit.GiB));
	}

}
