package org.nunocky.fakebrowser.bookmark;

import android.provider.BaseColumns;

public class Bookmark {
    public static final long ID_UNDEFINED = Long.MIN_VALUE;

    public static interface Columns extends BaseColumns {
        public static final String NAME = "name";
        public static final String URL = "url";
    }

    private long id;
    private String name;
    private String url;

    public Bookmark(long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public Bookmark(String name, String url) {
        this.id = ID_UNDEFINED;
        this.name = name;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
