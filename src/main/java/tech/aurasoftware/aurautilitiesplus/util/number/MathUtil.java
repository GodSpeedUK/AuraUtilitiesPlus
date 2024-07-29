package tech.aurasoftware.aurautilitiesplus.util.number;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This is used in enchants & prestige calculations
 */
public class MathUtil {


    private static final BigDecimal HALF = BigDecimal.valueOf(0.5);
    private static final BigDecimal ZERO_25 = BigDecimal.valueOf(0.25);

    public static NumberWrapper getCostOfLevel(NumberWrapper level, long baseCost, double increaseBy) {
        final BigDecimal bigIncreaseBy, bigLevel, bigBaseCost;
        bigLevel = level.toBigDecimal();
        bigIncreaseBy = BigDecimal.valueOf(increaseBy);
        bigBaseCost = BigDecimal.valueOf(baseCost);

        return NumberWrapper.of(bigLevel.multiply(bigIncreaseBy).add(bigBaseCost)).normalize();
    }

    public static NumberWrapper getMaxLevelsCanGo(
        NumberWrapper currentLevel, long baseCost, double increaseBy, NumberWrapper balance) {
        NumberWrapper currentPriceOfLevel = bigGetCostOfLevelSummed(currentLevel, baseCost, increaseBy);
        return bigGetMaxLevelsCanGo(baseCost, increaseBy, balance.add(currentPriceOfLevel)).subtract(currentLevel);
    }

    public static NumberWrapper getCostOfLevels(
        NumberWrapper currentLevel, long baseCost, double increaseBy, NumberWrapper levels) {

        NumberWrapper currentPriceOfLevel = bigGetCostOfLevelSummed(currentLevel, baseCost, increaseBy);
        return bigGetCostOfLevelSummed(currentLevel.add(levels), baseCost, increaseBy)
                .subtract(currentPriceOfLevel);
    }

    public static NumberWrapper getCostBetweenLevels(
        NumberWrapper level, long baseCost, double increaseBy, NumberWrapper level2) {
        NumberWrapper costOfLevel1 = bigGetCostOfLevelSummed(level, baseCost, increaseBy);
        NumberWrapper costOfLevel2 = bigGetCostOfLevelSummed(level2, baseCost, increaseBy);

        if (costOfLevel2.isMoreThan(costOfLevel1)) {
            return costOfLevel2.subtract(costOfLevel1);
        }

        return costOfLevel1.subtract(costOfLevel2);
    }

    public static NumberWrapper bigGetCostOfLevelSummed(
        NumberWrapper level, long baseCost, double increaseBy) {
        // 0.5Ix² + (0.5I+B)x + B
        final BigDecimal bigIncreaseBy, bigLevel, bigBaseCost;
        bigLevel = level.toBigDecimal();
        bigIncreaseBy = BigDecimal.valueOf(increaseBy);
        bigBaseCost = BigDecimal.valueOf(baseCost);

        BigDecimal firstEquation =
                HALF.multiply(bigIncreaseBy)
                        .multiply(BigDecimalMath.pow(bigLevel, 2, MathContext.DECIMAL64));
        BigDecimal secondEquation = HALF.multiply(bigIncreaseBy).add(bigBaseCost).multiply(bigLevel);
        return NumberWrapper.of(
                firstEquation
                        .add(secondEquation)
                        .add(bigBaseCost)
                        .round(MathContext.DECIMAL128)
                        .toBigInteger());
    }

    protected static NumberWrapper bigGetMaxLevelsCanGo(
            long baseCost, double increaseBy, NumberWrapper balance) {
        // (-0.5 * I - B - sqrt(0.25 * I^2 + 2 * C * I - B * I + B ^ 2)) / I
        final BigDecimal bigIncreaseBy, bigBaseCost;
        bigIncreaseBy = BigDecimal.valueOf(increaseBy);
        bigBaseCost = BigDecimal.valueOf(baseCost);

        final BigDecimal powIncreaseBy = pow(bigIncreaseBy, 2);
        final BigDecimal powBaseCost = pow(bigBaseCost, 2);

        // (0.25 * I^2 + 2 * C * I - B * I + B ^ 2)
        BigDecimal sqrtPart =
                sqrt(
                        ZERO_25
                                .multiply(powIncreaseBy)
                                .add(big(2).multiply(balance.toBigDecimal()).multiply(bigIncreaseBy))
                                .subtract(bigBaseCost.multiply(bigIncreaseBy))
                                .add(powBaseCost));

        return NumberWrapper.of(
                HALF.negate()
                        .multiply(bigIncreaseBy)
                        .subtract(bigBaseCost)
                        .add(sqrtPart)
                        .divide(bigIncreaseBy, MathContext.DECIMAL64)
                        .toBigInteger());
    }

    protected static BigDecimal big(Number number) {
        return BigDecimal.valueOf(number.doubleValue());
    }

    protected static BigDecimal pow(BigDecimal value, long times) {
        return BigDecimalMath.pow(value, times, MathContext.DECIMAL64);
    }

    protected static BigDecimal sqrt(BigDecimal value) {
        return BigDecimalMath.sqrt(value, MathContext.DECIMAL64);
    }
}
