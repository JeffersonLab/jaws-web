package org.jlab.jaws.presentation.util;

import javax.servlet.http.HttpServletRequest;
import org.jlab.smoothness.business.exception.UserFriendlyException;

public class FriendlyParamUtil {
  public static Boolean convertYNBoolean(HttpServletRequest request, String name)
      throws UserFriendlyException {
    String valueStr = request.getParameter(name);
    Boolean value = null;
    if (valueStr != null && !valueStr.isEmpty()) {
      if ("N".equals(valueStr)) {
        value = false;
      } else {
        if (!"Y".equals(valueStr)) {
          throw new UserFriendlyException("Value must be one of 'Y' or 'N'");
        }

        value = true;
      }
    }

    return value;
  }
}
