package org.nunocky.fakebrowser;

import android.app.Application;
import android.database.Cursor;

import org.nunocky.fakebrowser.bookmark.BookmarkDBAccess;

/**
 * Created by nunokawa on 15/01/23.
 */
public class AppController extends Application {
    private static final String TAG = "AppController";

    @Override
    public void onCreate() {
        super.onCreate();

        initDB();
    }

    private void initDB() {
        BookmarkDBAccess access = null;
        try {
            access = new BookmarkDBAccess(this);
            //access.deleteAll();
            Cursor c = access.queryAll();
            if (c.getCount() == 0) {
                access.create("Google", "http://www.google.com/");
                access.create("Yahoo", "http://www.yahoo.com/");
                access.create("Facebook", "http://www.facebok.com/");
                access.create("Twitter", "http://www.twitter.com/");
            }
            c.close();
        } finally {
            if (access != null) {
                access.close();
            }
        }
    }
}
