package com.poortorich.chart.util;

import java.text.DecimalFormat;

public class AmountFormatter {

    private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("###,###");
    private static final long TEN_THOUSAND = 10_000L;
    private static final String WON_SUFFIX = "원";
    private static final String TEN_THOUSAND_SUFFIX = "만원";

    public static String convertAmount(long amount) {
        if (amount >= TEN_THOUSAND) {
            return COMMA_FORMAT.format(amount / TEN_THOUSAND) + TEN_THOUSAND_SUFFIX;
        }

        return COMMA_FORMAT.format(amount) + WON_SUFFIX;
    }

    public static String compareAmount(long compareAmount, long averageAmount) {
        long differenceAmount = Math.abs(averageAmount - compareAmount);

        if (compareAmount > averageAmount) {
            return convertAmount(differenceAmount) + " 더";
        } else if (compareAmount < averageAmount) {
            return convertAmount(differenceAmount) + " 덜";
        }
        return convertAmount(differenceAmount);
    }
}
