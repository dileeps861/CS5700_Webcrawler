package webcrowler.helpers;

import java.util.Map;

/**
 * Utility class for string constants.
 */
public final class StringUtils {

    public final static String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public final static String CONTENT_TYPE = "Content-Type: %s\r\n";
    public final static String CONTENT_LENGTH = "Content-Length: %s\r\n";
    public final static String HOST = "Host:%s\r\n";
    public final static String REQUEST_PATH_HEADER = "%s %s %s\n";
    public final static String KEEP_CONNECTION_ALIVE = "Connection: keep-alive\r\n";
    public final static String COOKIE_SET = "Cookie: csrftoken=%s; sessionid=%s\r\n";


    // Constant field types and variables
    public final static String SIGN_IN_PATH = "/accounts/login/";
    public final static String FAKEBOOK_PATH = "/fakebook/";
    public final static String HOME_PATH = "/";
    public static final String REQUEST_TYPE = "HTTP/1.0";
    public static final String POST_METHOD = "POST";
    public static final String GET_METHOD = "GET";

    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";
    public static final String NEXT = "next";

    // Coockie constants
    public final static String CSRF_MIDDLEWARE_TOKEN = "csrfmiddlewaretoken";
    public final static String CSRF_TOKEN = "csrftoken";
    public final static String SESSION_ID = "sessionid";

    private StringUtils() {
    }


    public static String requestDataBuilder(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            sb.append(param.getKey());
            sb.append("=");
            sb.append(param.getValue());
            sb.append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
