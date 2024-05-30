package org.jlab.jaws.presentation.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TagFunctions {

    private TagFunctions() {
        // cannot instantiate publicly
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
