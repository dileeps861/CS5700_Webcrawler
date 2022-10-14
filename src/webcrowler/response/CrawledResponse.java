package webcrowler.response;

import java.util.HashSet;
import java.util.Set;

/**
 * @author shah.dile
 */
public class CrawledResponse {
    private final Set<String> links;
    private final Set<String> secretFlags;
    private int statusCode;
    private String statusMessage;
    private String csrfToken;
    private String sessionId;
    private String csrfMiddlewareToken;

    public CrawledResponse() {
        this.statusCode = 404;
        this.statusMessage = "";
        this.links = new HashSet<>();
        this.secretFlags = new HashSet<>();
    }

    public void addLink(String link) {
        this.links.add(link);
    }

    public void addSecretFlag(String secretFlag) {
        this.secretFlags.add(secretFlag);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCsrfMiddlewareToken() {
        return csrfMiddlewareToken;
    }

    public void setCsrfMiddlewareToken(String csrfMiddlewareToken) {
        this.csrfMiddlewareToken = csrfMiddlewareToken;
    }

    public Set<String> getLinks() {
        return new HashSet<>(links);
    }

    public Set<String> getSecretFlags() {
        return secretFlags;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CrawledResponse{");
        sb.append("statusCode=").append(statusCode);
        sb.append(", statusMessage='").append(statusMessage).append('\'');
        sb.append(", links=").append(links);
        sb.append(", csrfToken='").append(csrfToken).append('\'');
        sb.append(", sessionId='").append(sessionId).append('\'');
        sb.append(", csrfMiddlewareToken='").append(csrfMiddlewareToken).append('\'');
        sb.append(", secretFlags=").append(secretFlags);
        sb.append('}');
        return sb.toString();
    }
}
