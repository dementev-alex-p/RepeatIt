package com.github.dementev_alex_p.repeatit.utils;

public class NumberTextConverter {



    public static String convert(final int number) {
       return switch (number) {
           case 1 -> "1⃣";
           case 2 -> "2⃣";
           case 3 -> "3⃣";
           case 4 -> "4⃣";
           case 5 -> "5⃣";
           case 6 -> "6⃣";
           case 7 -> "7⃣";
           case 8 -> "8⃣";
           case 9 -> "9⃣";
           case 10 -> "\uD83D\uDD1F";
           default -> throw new IllegalStateException("Unexpected value: " + number);
       };
    }


}
