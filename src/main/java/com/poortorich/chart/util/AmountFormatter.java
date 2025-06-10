package com.poortorich.chart.util;

import java.text.DecimalFormat;

public class AmountFormatter {

    private static final DecimalFormat COMMA_FORMAT = new DecimalFormat("###,###");

    private static final long TEN_THOUSAND = 10_000L;
    private static final long HUNDRED_MILLION = 100_000_000L;
    private static final long TRILLION = 1_000_000_000_000L;

    private static final String WON_SUFFIX = "원";
    private static final String TEN_THOUSAND_SUFFIX = "만원";
    private static final String HUNDRED_MILLION_SUFFIX = "억";
    private static final String TRILLION_SUFFIX = "조";

    public static String convertAmount(long amount) {
        if (amount >= TRILLION) {
            return COMMA_FORMAT.format(amount / TRILLION) + TRILLION_SUFFIX;
        } else if (amount >= HUNDRED_MILLION) {
            return COMMA_FORMAT.format(amount / HUNDRED_MILLION) + HUNDRED_MILLION_SUFFIX;
        } else if (amount >= TEN_THOUSAND) {
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
