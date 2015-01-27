package org.nunocky.fakebrowser.event;

/**
 * Created by nunokawa on 15/01/23.
 */
public class EditBookmarkEvent {
    private int mId;

    public EditBookmarkEvent(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }
}
