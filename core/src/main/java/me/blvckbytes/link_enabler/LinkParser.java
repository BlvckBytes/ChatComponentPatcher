package me.blvckbytes.link_enabler;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

  // Credits for the parsing-logic go to: https://github.com/festino-mc-plugins/ClickableLinks
  // TODO: This could still use some cleanup as well as a few test-cases, :)

  private static final String LINK_REGEX = "(((?:https?):\\/\\/)?(?:(?:[-a-z0-9_а-яА-Я]{1,}\\.){1,}([a-z0-9а-яА-Я]{1,}).*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + " \\n]|$))))";
  private static final Pattern PATTERN = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
  private static final int SCHEME_GROUP_INDEX = 2;
  private static final int TLD_GROUP_INDEX = 3;
  private static final boolean useIps = false;
  private static final boolean useLinks = true;

  public static final String DEFAULT_PROTOCOL = "https://";

  public static Matcher matchLinks(String message) {
    return PATTERN.matcher(message);
  }

  public static @Nullable String tryParseLink(String message, Matcher matcher) {
    int tldStart = matcher.start(TLD_GROUP_INDEX);
    int tldEnd = matcher.end(TLD_GROUP_INDEX);
    String tld = message.substring(tldStart, tldEnd);
    int domainsStart = matcher.end(SCHEME_GROUP_INDEX);
    if (domainsStart < 0)
      domainsStart = matcher.start();
    if (hasNumber(tld)) {
      if (!useIps || !isValidIP(message, domainsStart, tldEnd))
        return null;
    } else if (!useLinks || !isValidTLD(tld)) {
      return null;
    }

    String url = message.substring(matcher.start(), matcher.end());
    boolean hasProtocol = matcher.end(SCHEME_GROUP_INDEX) - matcher.start(SCHEME_GROUP_INDEX) > 0;
    return hasProtocol ? url : DEFAULT_PROTOCOL + url;
  }

  private static boolean isValidTLD(String tld) {
    return tld.length() >= 2;
  }

  private static boolean isValidIP(String str, int begin, int end) {
    if (end - begin < 7)
      return false;
    int val = 0;
    int count = 1;
    for (int i = begin; i < end; i++) {
      char c = str.charAt(i);
      if (c == '.') {
        count++;
        val = 0;
        continue;
      }
      if (!Character.isDigit(c))
        return false;
      int digit = c - '0';
      val = val * 10 + digit;
      if (val > 255)
        return false;
    }
    return count == 4 && str.charAt(end - 1) != '.';
  }

  private static boolean hasNumber(String str) {
    int length = str.length();
    for (int i = 0; i < length; i++)
      if (Character.isDigit(str.charAt(i)))
        return true;
    return false;
  }
}
