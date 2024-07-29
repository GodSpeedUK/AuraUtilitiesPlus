package tech.aurasoftware.aurautilitiesplus.util.number;

import com.google.common.primitives.Doubles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {
    public static final String[] suffixes =
            new String[]{"", "k", "M", "B", "T", "Q", "Qt", "S", "ST", "O", "N", "D", "UD", "DD", "Z"};
    private static final Pattern charactersPattern = Pattern.compile("[a-zA-Z]+");
    private static final Pattern numbersPattern = Pattern.compile("[0-9]\\d{0,100}(\\.\\d{1,3})?");
    private static final BigDecimal BIG_DECIMAL_THOUSAND = BigDecimal.valueOf(1000);

    public static String formatBigDecimal(BigDecimal number) {
        if (number.compareTo(BIG_DECIMAL_THOUSAND) < 0) {
            return number.toBigInteger().intValue() + "";
        }

        number = number.setScale(0, RoundingMode.DOWN);

        String numberString = number.stripTrailingZeros().toBigInteger().toString();
        int suffixIndex = number.toPlainString().length() / 3;

        if (suffixIndex == 0) {
            return numberString;
        }

        number = number.divide(BIG_DECIMAL_THOUSAND.pow(suffixIndex));

        // If the number is less than 1, we move suffix down by one
        if (number.compareTo(BigDecimal.ONE) < 0) {
            suffixIndex--;
            number = number.multiply(BIG_DECIMAL_THOUSAND);
        }

        // To round it to 2 digits.
        BigDecimal bigDecimal = number;
        bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_FLOOR);

        // Add the number with the denomination to get the final value.
        return bigDecimal.stripTrailingZeros().toPlainString() + getSuffixes().get(suffixIndex);
    }

    private static List<String> extractMatches(String input, Pattern pattern) {
        List<String> matches = new LinkedList<>();
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    public static Optional<NumberWrapper> tryParse(String input) {
        try {
            return Optional.of(NumberWrapper.of(formattedToBigDecimal(input)));
        } catch (Throwable throwable) {
            return Optional.empty();
        }
    }

    public static BigDecimal formattedToBigDecimal(String input) {
        input = input.replace(" ", "");
        input = input.replace(",", "");
        input = input.replace("\\.", "");
        List<String> numbers = extractMatches(input, numbersPattern);
        List<String> characters = extractMatches(input, charactersPattern);

        if (characters.size() == 0 && numbers.size() == 1) {
            return BigDecimal.valueOf(Doubles.tryParse(numbers.get(0)));
        }

        if (characters.size() != numbers.size()) {
            throw new IllegalStateException(
                    "Failed to parse a number from " + input + " cause invalid format!");
        }

        BigDecimal value = BigDecimal.ZERO;
        for (int i = 0; i < characters.size(); i++) {
            BigDecimal number = BigDecimal.valueOf(Doubles.tryParse(numbers.get(i)));
            int suffixIndex = getSuffixes().indexOf(characters.get(i));
            if (suffixIndex == -1) {
                throw new IllegalStateException(
                        "Failed to parse number from " + input + " cause invalid suffix: " + characters.get(i));
            }

            value =
                    value.add(number.multiply(BIG_DECIMAL_THOUSAND.pow(suffixIndex))).stripTrailingZeros();
        }

        return value;
    }

    private static List<String> getSuffixes() {
        return Arrays.asList(suffixes);
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
