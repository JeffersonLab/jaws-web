package org.jlab.jaws.presentation.util;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class TagFunctions {

  private TagFunctions() {
    // cannot instantiate publicly
  }

  public static String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  public static String addS(int x) {
    if (x != 1) {
      return "s";
    } else {
      return "";
    }
  }

  public static String millisToHumanReadable(long milliseconds) {
    String time;
    if (milliseconds < 60000) {
      int seconds = (int) Math.floor(milliseconds / 1000);
      time = seconds + " second" + addS(seconds);
    } else {
      int hours = (int) Math.floor((milliseconds) / 3600000),
          remainingMilliseconds = (int) (milliseconds % 3600000),
          minutes = (int) Math.floor(remainingMilliseconds / 60000);

      time =
          (hours > 0 ? hours + " hour" + addS(hours) + " " : "")
              + minutes
              + " minute"
              + addS(minutes);
    }

    return time;
  }

  public static String getHostnameFromIp(String ip) {
    String hostname = ip;

    if (ip != null) {
      try {
        InetAddress address = InetAddress.getByName(ip);
        hostname = address.getHostName();

        if (!ip.equals(hostname)) {
          hostname = hostname + " (" + ip + ")";
        }
      } catch (UnknownHostException e) {
        // Unable to resolve... oh well, just use ip
      }
    }

    return hostname;
  }
}
