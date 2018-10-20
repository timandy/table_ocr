package com.bestvike.ocr.util;

import com.bestvike.ocr.collection.CharSequenceIterable;

/**
 * Created by 许崇雷 on 2017-11-21.
 */
public final class HtmlUtils extends org.springframework.web.util.HtmlUtils {
    private static String htmlEscapeChar(char ch) {
        switch (ch) {
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '&':
                return "&amp;";
            case '\'':
                return "&#39;";
            case ' ':
            case '　':
                return "&nbsp;";
            default:
                return String.valueOf(ch);
        }
    }

    public static String htmlEscape(String input) {
        if (input == null)
            return null;
        StringBuilder builder = new StringBuilder(input.length() * 2);
        for (char ch : new CharSequenceIterable(input))
            builder.append(htmlEscapeChar(ch));
        return builder.toString();
    }
}
