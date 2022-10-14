import webcrowler.cookies.Cookie;
import webcrowler.helpers.CrawlingHandler;
import webcrowler.helpers.RequestBuilder;
import webcrowler.helpers.StringUtils;
import webcrowler.parser.html.ResponseParser;
import webcrowler.response.CrawledResponse;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static webcrowler.helpers.StringUtils.*;

public class CrawlerClient {
    private final String userName;
    private final String password;
    private final Cookie cookie;
    private final Set<String> secretFlags;
    private final CrawlingHandler crawlingHandler;

    public CrawlerClient(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.cookie = new Cookie();
        this.secretFlags = new HashSet<>();
        this.crawlingHandler = new CrawlingHandler();
    }

    public static void main(String[] args) {
        if (args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Error: Must provide <user name> <password>");
            System.exit(1);
        }
        CrawlerClient client = new CrawlerClient(args[0], args[1]);
        try {
            client.run("project2.5700.network", 443);
        }
        catch (InterruptedException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private Socket createConnection(String host, int port) throws IOException {

        Socket socket = null;
        try {
            // Connect through ssl
            SocketFactory sslSocketFactory = SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            sslSocket.startHandshake();
            socket = sslSocket;

        }
        catch (IOException e) {
            System.err.println("Error: Failed to connect to " + host + ":" + port);
        }
        // If cannot connect through ssl, try to connect through normal socket
        if (socket == null || socket.isClosed()) {
            socket = new Socket(host, port);
        }
        return socket;
    }

    public void run(String host, int port) throws InterruptedException, IOException {
        // Crawler get log in page
        String res = getLoginPage(createConnection(host, port), host);
        CrawledResponse response = ResponseParser.parseResponse(res, true, true, crawlingHandler);
        secretFlags.addAll(response.getSecretFlags());

        cookie.addCookie(CSRF_TOKEN, response.getCsrfToken());
        cookie.addCookie(SESSION_ID, response.getSessionId());
        cookie.addCookie(CSRF_MIDDLEWARE_TOKEN, response.getCsrfMiddlewareToken());

        // Send login request to Fakebook
        res = sendLogInRequest(createConnection(host, port), host);
        response = ResponseParser.parseResponse(res, true, false, crawlingHandler);
        cookie.addCookie(CSRF_TOKEN, response.getCsrfToken());
        cookie.addCookie(SESSION_ID, response.getSessionId());
        secretFlags.addAll(response.getSecretFlags());

        res = getCrawledPage(createConnection(host, port), host, HOME_PATH);
        response = ResponseParser.parseResponse(res, false, false, crawlingHandler);
        secretFlags.addAll(response.getSecretFlags());
        // Start crawling!
        while (!crawlingHandler.queue.isEmpty()) {
            // Use BFS to crawl and using multithreading to speed up crawling.
            Thread t = new Thread(() -> {
                String finalUrl = crawlingHandler.getNextUrl();
                if (finalUrl == null || !finalUrl.startsWith("/fakebook/")) {
                    return;
                }
                String crawledPageResponse;
                try {
                    crawledPageResponse = getCrawledPage(createConnection(host, port), host, finalUrl);
                    CrawledResponse crawledResponseObj = ResponseParser.parseResponse(crawledPageResponse, false,
                            false, crawlingHandler);
                    while (crawledResponseObj.getStatusCode() == 500) { // Response is 500, so we need to retry until we get response 200/300
                        crawledPageResponse = getCrawledPage(createConnection(host, port), host, finalUrl);
                        crawledResponseObj = ResponseParser.parseResponse(crawledPageResponse, false, false, crawlingHandler);
                    }
                    if (crawledResponseObj.getStatusCode() == 200 || crawledResponseObj.getStatusCode() == 302 ||
                            crawledResponseObj.getStatusCode() == 301) {
                        secretFlags.addAll(crawledResponseObj.getSecretFlags());
                    }
                }
                catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            });
            t.start();
            Thread.sleep(10);
            // If the queue is empty, we have finished crawling from the thread before moving to next links
            if (crawlingHandler.queue.isEmpty() || crawlingHandler.queue.size() < 10) {
                // Wait for thread to finish finding secret flags and the links and add to queue.
                t.join();
            }

        }
        // Print out all the secret flags
        for (String flag : secretFlags) {
            System.out.println(flag);
        }
    }

    private String submitRequestAndGetResponse(Socket socket, RequestBuilder request) {
        try {
            //Prints the request string to the output streamReader and then flushes it socket
            PrintWriter wtr = new PrintWriter(socket.getOutputStream(), true);
            wtr.println(request.buildRequest());
            wtr.flush();

            // Reads the response from the input streamReader
            BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String outStr;
            StringBuilder responseData = new StringBuilder();
            while ((outStr = bufRead.readLine()) != null) {
                responseData.append(outStr).append("\n");
            }
            return responseData.toString();
        }
        catch (IOException e) {
            System.err.println("Error while sending request. Error: " + e.getMessage());
            return "";
        }
    }

    private String getLoginPage(Socket socket, String host) {
        RequestBuilder requestBuilder = RequestBuilder.builder()
                .setHost(host)
                .setMethod(GET_METHOD)
                .setPath(SIGN_IN_PATH)
                .build();
        return submitRequestAndGetResponse(socket, requestBuilder);
    }

    private String sendLogInRequest(Socket socket, String host) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(USERNAME, userName);
        params.put(PASSWORD, password);
        params.put(CSRF_MIDDLEWARE_TOKEN, cookie.getCookie(CSRF_MIDDLEWARE_TOKEN));
        params.put(NEXT, "");
        RequestBuilder requestBuilder = RequestBuilder.builder()
                .setHost(host)
                .setMethod(POST_METHOD)
                .setPath(SIGN_IN_PATH)
                .setCsrfToken(cookie.getCookie(CSRF_TOKEN))
                .setSessionId(cookie.getCookie(SESSION_ID))
                .setContentType(StringUtils.APPLICATION_X_WWW_FORM_URLENCODED)
                .setContent(StringUtils.requestDataBuilder(params))
                .build();
        return submitRequestAndGetResponse(socket, requestBuilder);
    }

    private String getCrawledPage(Socket socket, String host, String path) {

        RequestBuilder requestBuilder = RequestBuilder.builder()
                .setHost(host)
                .setMethod(GET_METHOD)
                .setPath(path)
                .setCsrfToken(cookie.getCookie(CSRF_TOKEN))
                .setSessionId(cookie.getCookie(SESSION_ID))
                .build();
        return submitRequestAndGetResponse(socket, requestBuilder);
    }
}
