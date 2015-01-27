package org.nunocky.fakebrowser.event;

/**
 * Created by nunokawa on 15/01/23.
 */
public class UpdateBookmarkEvent {
    private long id;
    private String name, url;

    public UpdateBookmarkEvent(long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
