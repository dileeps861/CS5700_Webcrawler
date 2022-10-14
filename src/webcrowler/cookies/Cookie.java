package webcrowler.cookies;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a cookie. It is used to store the cookie name, and it's value.
 *
 * @author shah.dile
 */
public class Cookie {
    private final Map<String, String> cookies;

    public Cookie() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(String cookie) {
        String[] cookieArray = cookie.split(";");
        for (String cookieString : cookieArray) {
            String[] cookieKeyValue = cookieString.split("=");
            if (cookieKeyValue.length == 2) {
                cookies.put(cookieKeyValue[0].trim(), cookieKeyValue[1].trim());
            }
        }
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    /**
     * Returns the value of the cookie with the given name.
     *
     * @param name the name of the cookie.
     * @return the value of the cookie with the given name.
     */
    public String getCookie(String name) {
        if (cookies.containsKey(name)) {
            return cookies.get(name);
        }
        return "";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Cookies=").append(cookies);
        return sb.toString();
    }
}
