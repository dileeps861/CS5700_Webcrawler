package webcrowler.parser.html;

import webcrowler.helpers.CrawlingHandler;
import webcrowler.helpers.StringUtils;
import webcrowler.response.CrawledResponse;

import java.util.Calendar;

/**
 * Utility class to parse the response from the request.
 *
 * @author shah.dile
 */
public final class ResponseParser {
    private ResponseParser() {
    }

    public static CrawledResponse parseResponse(String responseString, boolean hasCookies, boolean hasCsrfMiddlewareToken,
                                                CrawlingHandler crawlingHandler) {
        Calendar calendar = Calendar.getInstance();
        CrawledResponse crawledResponse = new CrawledResponse();
        parseResponse(responseString, crawledResponse, hasCookies, hasCsrfMiddlewareToken, crawlingHandler);
        return crawledResponse;
    }

    private static void parseResponse(String responseString, CrawledResponse crawledResponseObj,
                                      boolean hasCookies, boolean hasCsrfMiddlewareToken, CrawlingHandler crawlingHandler) {
        String[] responseLines = responseString.split("\n");
        String[] statusLine = responseLines[0].split(" ");
        crawledResponseObj.setStatusCode(Integer.parseInt(statusLine[1]));
        crawledResponseObj.setStatusMessage(statusLine[2]);

        for (String responseLine : responseLines) {
            responseLine = responseLine.trim();

            if (responseLine.isEmpty()) {
                continue;
            }
            if (responseLine.startsWith("<a") || responseLine.startsWith("<li><a")) {
                int startIndex = responseLine.indexOf("\"") + 1;
                int lastIndex = responseLine.indexOf("\"", startIndex);
                String link = responseLine.substring(startIndex, lastIndex).trim();
                crawlingHandler.addUrl(link);

                // crawledResponseObj.addLink();
            }
            else if (responseLine.startsWith("<h2") && responseLine.contains("secret_flag")
                    && responseLine.contains("color:red")) {
                int firsIndex = responseLine.indexOf(">");
                firsIndex = responseLine.indexOf("FLAG: ", firsIndex + 1) + "FLAG: ".length();
                int lastIndex = responseLine.indexOf("<", firsIndex);
                String secretFlag = responseLine.substring(firsIndex, lastIndex);
                crawledResponseObj.addSecretFlag(secretFlag);
            }
            if (crawledResponseObj.getStatusCode() == 200 || crawledResponseObj.getStatusCode() == 302) {

                if (hasCookies && crawledResponseObj.getCsrfToken() == null && responseLine.startsWith("Set-Cookie: csrftoken=")) {
                    retrieveCookie(responseLine, StringUtils.CSRF_TOKEN, crawledResponseObj);
                }
                else if (hasCookies && crawledResponseObj.getSessionId() == null && responseLine.startsWith("Set-Cookie: sessionid=")) {
                    retrieveCookie(responseLine, StringUtils.SESSION_ID, crawledResponseObj);
                }
                else if (hasCsrfMiddlewareToken && crawledResponseObj.getCsrfMiddlewareToken() == null) {
                    retrieveCsrfMiddlewareToken(responseLine, crawledResponseObj);
                }
            }

        }
    }

    private static void retrieveCsrfMiddlewareToken(String responseLine, CrawledResponse crawledResponseObj) {
        int csrfTokenIndex = responseLine.indexOf(StringUtils.CSRF_MIDDLEWARE_TOKEN);
        if (csrfTokenIndex != -1) {
            int csrfTokenValueIndex = responseLine.indexOf("value", csrfTokenIndex);
            if (csrfTokenValueIndex != -1) {
                int csrfTokenValueStartIndex = responseLine.indexOf("\"", csrfTokenValueIndex);
                if (csrfTokenValueStartIndex != -1) {
                    int csrfTokenValueEndIndex = responseLine.indexOf("\"", csrfTokenValueStartIndex + 1);
                    if (csrfTokenValueEndIndex != -1) {
                        crawledResponseObj.setCsrfMiddlewareToken(
                                responseLine.substring(csrfTokenValueStartIndex + 1, csrfTokenValueEndIndex));
                    }
                }
            }
        }
    }

    private static void retrieveCookie(String responseLine, String cookieName, CrawledResponse crawledResponseObj) {
        int cookieValueStartIndex = responseLine.indexOf("=");
        if (cookieValueStartIndex != -1) {
            int cookieValueEndIndex = responseLine.indexOf(";");
            if (cookieValueEndIndex != -1) {
                String cookie = responseLine.substring(cookieValueStartIndex + 1, cookieValueEndIndex);
                if (cookieName.equals(StringUtils.CSRF_TOKEN)) {
                    crawledResponseObj.setCsrfToken(cookie);
                }
                else if (cookieName.equals(StringUtils.SESSION_ID)) {
                    crawledResponseObj.setSessionId(cookie);
                }
            }
        }
    }

//    private static void retrieveLinks(String responseString, CrawledResponse response) {
//        String[] responseLines = responseString.split("\n");
//        for (String responseLine : responseLines) {
//            if (responseLine.contains("<a href=")) {
//                String[] hrefPartArray = responseLine.split("<a href=");
//                for (String hrefPart : hrefPartArray) {
//                    response.addLink(hrefPart.split(">")[0].replace("\"", ""));
//                }
//            }
//        }
//    }
}
