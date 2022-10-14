package webcrowler.helpers;

import static webcrowler.helpers.StringUtils.CONTENT_LENGTH;
import static webcrowler.helpers.StringUtils.CONTENT_TYPE;
import static webcrowler.helpers.StringUtils.COOKIE_SET;
import static webcrowler.helpers.StringUtils.HOST;
import static webcrowler.helpers.StringUtils.KEEP_CONNECTION_ALIVE;
import static webcrowler.helpers.StringUtils.REQUEST_PATH_HEADER;
import static webcrowler.helpers.StringUtils.REQUEST_TYPE;

/**
 * Utility class to create HTTP requests.
 */
public class RequestBuilder {

    private final String host;
    private final String path;
    private final String method;
    private final String contentType;
    private final int contentLength;
    private final String content;
    private final String sessionId;
    private final String csrfToken;

    private RequestBuilder(String host, String path, String method, String contentType,
                           String content, String sessionId, String csrfToken) {
        this.host = host;
        this.path = path;
        this.method = method;
        this.contentType = contentType;
        this.content = content != null ? content : "";
        this.contentLength = this.content.length();
        this.sessionId = sessionId != null ? sessionId : "";
        this.csrfToken = csrfToken != null ? csrfToken : "";
    }

    public static Builder builder() {
        return new Builder();
    }

    public String buildRequest() {
        StringBuilder request = new StringBuilder();
        request.append(String.format(REQUEST_PATH_HEADER, method, path, REQUEST_TYPE));
        request.append(String.format(HOST, host));
        if (!content.isEmpty()) {
            request.append(String.format(CONTENT_TYPE, contentType));
            request.append(String.format(CONTENT_LENGTH, contentLength));
        }
        if (!sessionId.isEmpty() && !csrfToken.isEmpty()) {
            request.append(String.format(COOKIE_SET, csrfToken, sessionId));
        }
        request.append("\r").append("\n");
        request.append(content);
        return request.toString();
    }

    @Override
    public String toString() {
        return buildRequest();
    }

    /**
     * Creates builder to build {@link RequestBuilder}.
     */
    public static class Builder {
        private String host;
        private String path;
        private String method;
        private String contentType;
        private String content;
        private String sessionId;
        private String csrfToken;

        /**
         * Constructor for {@link Builder}.
         */
        private Builder() {
        }

        /**
         * Sets the {@code host} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param host the host name to connect
         * @return a reference to this Builder
         */
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets the path and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param path the path
         * @return builder reference to this Builder
         */
        public Builder setPath(String path) {
            this.path = path;
            return this;
        }


        /**
         * Sets the request method and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param method the method name GET or POST or etc.
         * @return builder reference to this Builder
         */
        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        /**
         * Sets the {@code contentType} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param contentType the {@code contentType} to set
         * @return a reference to this Builder
         */
        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Sets the {@code content} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param content the {@code content} to set
         * @return a reference to this Builder
         */
        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        /**
         * Sets the {@code sessionId} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param sessionId the {@code sessionId} to set
         * @return a reference to this Builder
         */
        public Builder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        /**
         * Sets the {@code csrfToken} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param csrfToken the {@code csrfToken} to set
         * @return a reference to this Builder
         */
        public Builder setCsrfToken(String csrfToken) {
            this.csrfToken = csrfToken;
            return this;
        }

        /**
         * Builds object @link RequestBuilder} and returns.
         *
         * @return the built RequestBuilder object.
         */
        public RequestBuilder build() {
            return new RequestBuilder(host, path, method, contentType, content, sessionId, csrfToken);
        }
    }

}
