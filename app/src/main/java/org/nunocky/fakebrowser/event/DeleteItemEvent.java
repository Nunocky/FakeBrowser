package org.nunocky.fakebrowser.event;

/**
 * Created by nunokawa on 15/01/23.
 */
public class DeleteItemEvent {
    private static final String TAG = "DeleteItemEvent";
    private long id;

    public DeleteItemEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
