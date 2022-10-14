package webcrowler.helpers;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class CrawlingHandler {
    public final Queue<String> queue;
    private final Set<String> visited;

    public CrawlingHandler() {
        this.queue = new ArrayDeque<>();
        this.visited = new HashSet<>();
        prePopulateAvoidedLinksInVisited();
    }

    private void prePopulateAvoidedLinksInVisited() {
        visited.addAll(Set.of(
                "http://www.w3.org/1999/xhtml",
                "http://www.w3.org/2000/svg",
                "http://www.w3.org/2000/xmlns/",
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
                "/accounts/logout/",
                "/accounts/login/",
                "http://khoury.northeastern.edu",
                "mailto:",
                "https://www.facebook.com/",
                "mailto:systems-stt-admin@zimbra.ccs.neu.edu",
                "/"
        ));
    }

    synchronized public void addUrl(String url) {
        if (!visited.contains(url.trim())) {
            queue.add(url.trim());
        }
    }

    synchronized public String getNextUrl() {
        if (queue.isEmpty()) {
            return null;
        }
        String url = queue.poll();
        visited.add(url);
        queue.removeAll(visited);
        Set<String> s = new HashSet<>(queue);
        queue.clear();
        queue.addAll(s);
        return url;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CrawlingHandler{");
        sb.append("queue=").append(queue);
        sb.append(", visited=").append(visited);
        sb.append('}');
        return sb.toString();
    }
}
