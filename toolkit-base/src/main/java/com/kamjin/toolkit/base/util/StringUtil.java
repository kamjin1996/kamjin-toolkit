package com.kamjin.toolkit.base.util;

/**
 * @author kam
 * @since 2021/4/14
 *
 * <p>
 * string工具类
 * </p>
 */
public final class StringUtil {

   public static String format(String formatKey, String... keyValues) {
        if (keyValues != null && keyValues.length != 0) {
            StringBuilder key = new StringBuilder();
            char[] chars = formatKey.toCharArray();
            int index = -1;
            boolean inmark = false;
            boolean firstinmark = false;

            for (int i = 0; i < chars.length; ++i) {
                char ch = chars[i];
                if (ch == 123) {
                    ++index;
                    inmark = true;
                    firstinmark = true;
                } else if (ch == 125) {
                    inmark = false;
                } else if (inmark) {
                    if (firstinmark) {
                        firstinmark = false;
                        key.append(keyValues[index]);
                    }
                } else {
                    key.append(chars[i]);
                }
            }

            return key.toString();
        } else {
            return formatKey;
        }
    }

      public static String concat(boolean isNullToEmpty, CharSequence... strs) {
        final StringBuilder sb = new StringBuilder();
        for (CharSequence str : strs) {
            sb.append(isNullToEmpty ? nullToEmpty(str) : str);
        }
        return sb.toString();
    }

    public static String nullToEmpty(CharSequence str) {
        return (str == null) ? "" : str.toString();
    }

}
