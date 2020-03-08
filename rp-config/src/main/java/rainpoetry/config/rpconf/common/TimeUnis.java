package rainpoetry.config.rpconf.common;


import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author RainPoetry
 */

public class TimeUnis {

	private static final String pattern = "(-?[0-9]+)([a-z]+)?";

	private static final Map<String, TimeUnit> timeSuffix;

	static {
		timeSuffix = Maps.newHashMap()
				.pairs("us", TimeUnit.MICROSECONDS)
				.pairs("ms",TimeUnit.MILLISECONDS)
				.pairs("s", TimeUnit.SECONDS)
				.pairs("m", TimeUnit.MINUTES)
				.pairs("min", TimeUnit.MINUTES)
				.pairs("h", TimeUnit.HOURS)
				.pairs("d", TimeUnit.DAYS)
				.unModify();
	}

	public static long timeStringAs(String format, TimeUnit unit) {
		String lower = format.toLowerCase().trim();
		if (match(lower)) {
			Matcher matcher = Pattern.compile(pattern).matcher(lower);
			if (matcher.find()) {
				String suffix = matcher.group(2);
				long number = Long.parseLong(matcher.group(1));
				if (suffix != null) {
					return unit.convert(number, timeSuffix.get(suffix));
				} else {
					return unit.convert(number, unit);
				}
			}
		}
		throw new NumberFormatException("Failed to parse time string: " + format);
	}

	public static boolean match(String format) {
		return format.matches(pattern);
	}

	public static long timeStringAsMs(String str) {
		return timeStringAs(str, TimeUnit.MILLISECONDS);
	}

	public static long timeStringAsSec(String str) {
		return timeStringAs(str, TimeUnit.SECONDS);
	}

}
