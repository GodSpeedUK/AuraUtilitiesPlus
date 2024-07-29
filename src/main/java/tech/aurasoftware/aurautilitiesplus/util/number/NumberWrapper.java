package tech.aurasoftware.aurautilitiesplus.util.number;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.gson.internal.Primitives;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class NumberWrapper {

  public static final Comparator<NumberWrapper> COMPARATOR = NumberWrapper::compareTo;

  private Number number;

  private NumberWrapper() {}

  public static NumberWrapper of(Number number) {
    NumberWrapper wrapper = new NumberWrapper();
    wrapper.number = number;
    return wrapper;
  }

  public static Optional<NumberWrapper> of(String input) {
    Number number = Helper.tryParse(input);
    if (number == null) return Optional.empty();

    NumberWrapper wrapper = new NumberWrapper();
    wrapper.number = number;
    return Optional.of(wrapper);
  }

  public boolean isMoreOrEquals(NumberWrapper to) {
    int i = compareTo(to);
    return i >= 0;
  }

  public boolean isLessOrEquals(NumberWrapper to) {
    int i = compareTo(to);
    return i <= 0;
  }

  public boolean equals(NumberWrapper to) {
    int i = compareTo(to);
    return i == 0;
  }

  public boolean isMoreThan(NumberWrapper to) {
    int i = compareTo(to);
    return i > 0;
  }

  public boolean isLessThan(NumberWrapper to) {
    int i = compareTo(to);
    return i < 0;
  }

  public long toLong() {
    return number.longValue();
  }

  public double toDouble() {
    return number.doubleValue();
  }

  public int toInt() {
    return number.intValue();
  }

  public Number toNumber() {
    return number;
  }

  public String formatWithCommas() {
    if (isBigInt() || isLong() || isInt())
      return String.format("%,d", number);
    return String.format("%,.0f", number);
  }

  public String formatWithSuffixes() {
    return NumberUtil.formatBigDecimal(toBigDecimal());
  }

  public NumberWrapper add(Number number) {
    return NumberWrapper.of(
        Helper.normalize(toBigDecimal().add(Helper.convertToBigDecimal(number))));
  }

  public NumberWrapper add(NumberWrapper number) {
    return add(number.toNumber());
  }

  public NumberWrapper subtract(NumberWrapper number) {
    return subtract(number.toNumber());
  }

  public NumberWrapper subtract(Number number) {
    return NumberWrapper.of(
        Helper.normalize(toBigDecimal().subtract(Helper.convertToBigDecimal(number))));
  }

  public NumberWrapper normalize() {
    return NumberWrapper.of(Helper.normalize(number));
  }

  public NumberWrapper max(NumberWrapper other) {
    if (isMoreThan(other)) {
      return this;
    }

    return other;
  }

  public NumberWrapper min(NumberWrapper other) {
    if (isMoreThan(other)) {
      return other;
    }

    return this;
  }

  @Override
  public String toString() {
    return number.toString();
  }

  public BigInteger toBigInt() {
    if (number instanceof BigInteger) return (BigInteger) number;
    return Helper.convertToBigDecimal(number).toBigInteger();
  }

  public BigDecimal toBigDecimal() {
    return Helper.convertToBigDecimal(number);
  }

  public boolean isInt() {
    return Primitives.unwrap(number.getClass()) == int.class;
  }

  public boolean isLong() {
    return Primitives.unwrap(number.getClass()) == long.class;
  }

  public boolean isDouble() {
    return Primitives.unwrap(number.getClass()) == double.class;
  }

  public boolean isBigInt() {
    return number instanceof BigInteger;
  }

  public boolean isBigDecimal() {
    return number instanceof BigDecimal;
  }

  public Class<? extends Number> getType() {
    return number.getClass();
  }

  public int compareTo(Number number) {
    return toBigDecimal().compareTo(Helper.convertToBigDecimal(number));
  }

  public int compareTo(NumberWrapper o) {
    return toBigDecimal().compareTo(o.toBigDecimal());
  }


  private static class Helper {
    // Parsers from smallest number to biggest
    private static final Function<String, Number>[] non_decimal_parsers =
        new Function[] {
          in -> Ints.tryParse(String.valueOf(in)),
          in -> Longs.tryParse(String.valueOf(in)),
          in -> new BigInteger(String.valueOf(in))
        };

    // Decimal parsers from smallest number to biggest
    private static final Function<String, Number>[] decimal_parsers =
        new Function[] {
          in -> Floats.tryParse(String.valueOf(in)),
          in -> Doubles.tryParse(String.valueOf(in)),
          in -> new BigDecimal(String.valueOf(in))
        };

    private static final Map<Integer, Function<String, Number>> non_decimal_normalizers =
        new HashMap<>();
    private static final Map<Integer, Function<String, Number>> decimal_normalizers =
        new HashMap<>();
    private static final DecimalFormat format_without_commas = new DecimalFormat("###.##");

    static {
      non_decimal_normalizers.put(10, Ints::tryParse);
      non_decimal_normalizers.put(19, Longs::tryParse);

      decimal_normalizers.put(17, Doubles::tryParse);
    }

    private static BigDecimal convertToBigDecimal(Number number) {
      if (number instanceof BigDecimal) return (BigDecimal) number;

      if (number instanceof BigInteger) return new BigDecimal(((BigInteger) number));

      if (number instanceof Double) return BigDecimal.valueOf(number.doubleValue());

      if (number instanceof Integer) return new BigDecimal(number.intValue());

      return new BigDecimal(number.toString());
    }

    public static Number tryParse(String input) {
      try {
        // If "string" contains . parse as decimal
        if (StringUtils.contains(".", input)) return useParsers(decimal_parsers, input);
        else return useParsers(non_decimal_parsers, input);

      } catch (Throwable throwable) {
        return null;
      }
    }

    private static Number useParsers(Function<String, Number>[] parsers, String what) {
      for (Function<String, Number> parser : parsers) {
        Number apply = parser.apply(what);
        if (apply != null) return apply;
      }

      return null;
    }

    public static Number normalize(Number number) {
      String format = format_without_commas.format(number);
      int length = format.length();

      if (!format.contains(".")) {
        for (Map.Entry<Integer, Function<String, Number>> normalizerEntry :
            non_decimal_normalizers.entrySet()) {
          if (normalizerEntry.getKey() <= length) {
            try {
              Number parsed = normalizerEntry.getValue().apply(format);
              if (parsed != null) return parsed;
            } catch (Throwable throwable) {
              // In case it fails to parse as seen in some situations,
              // We just skip this normalizer
            }
          }
        }

        // If none succeed to normalize we return BigInt
        return new BigInteger(format);
      }

      length = StringUtils.split(format, ".")[0].length();
      for (Map.Entry<Integer, Function<String, Number>> normalizerEntry :
          decimal_normalizers.entrySet()) {
        if (normalizerEntry.getKey() <= length) {
          try {
            Number parsed = normalizerEntry.getValue().apply(format);
            if (parsed != null) return parsed;
          } catch (Throwable throwable) {
            // In case it fails to parse as seen in some situations,
            // We just skip this normalizer
          }
        }
      }

      return new BigDecimal(format);
    }
  }
}
