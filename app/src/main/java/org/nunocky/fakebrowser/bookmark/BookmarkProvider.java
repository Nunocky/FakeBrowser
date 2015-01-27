package org.nunocky.fakebrowser.bookmark;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.text.TextUtils;

public class BookmarkProvider extends ContentProvider {
    private static final String AUTHORITY = "org.nunocky.fakebrowser.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/bookmark");

    // Custom MIME Type (vendor specific MIME type)... type/subtype
    public static final String MIME_DIR = "vnd.android.cursor.dir/" + AUTHORITY + ".bookmark";
    public static final String MIME_ITEM = "vnd.android.cursor.item/" + AUTHORITY + ".bookmark";

    private static final int IDX_MIME_DIR = 1;
    private static final int IDX_MIME_ITEM = 2;

    private static final UriMatcher sUriMatcher;

    private BookmarkDBAccess mBookmarkDBAccess;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "bookmark", IDX_MIME_DIR);
        sUriMatcher.addURI(AUTHORITY, "bookmark/#", IDX_MIME_ITEM);
    }

    public BookmarkProvider() {
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case IDX_MIME_DIR:
                return MIME_DIR;

            case IDX_MIME_ITEM:
                return MIME_ITEM;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Bookmark bm = mBookmarkDBAccess.create(values.getAsString(Bookmark.Columns.NAME), values.getAsString(Bookmark.Columns.URL));

        if (bm != null && bm.getId() > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, bm.getId());
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to insert row info " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        switch (sUriMatcher.match(uri)) {
            case IDX_MIME_DIR:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = Bookmark.Columns._ID + " ASC";
                }
                break;
            case IDX_MIME_ITEM:
                if (TextUtils.isEmpty(selection)) {
                    selection = Bookmark.Columns._ID + "=?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor c = mBookmarkDBAccess.query(projection, selection, selectionArgs, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int count;
        switch (sUriMatcher.match(uri)) {
            case IDX_MIME_ITEM:
                // throw new UnsupportedOperationException("Not yet implemented");
                final long bookmark_id = Long.parseLong(uri.getLastPathSegment());
                count = mBookmarkDBAccess.delete(bookmark_id);
                break;

            case IDX_MIME_DIR:
                count = mBookmarkDBAccess.delete(selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public boolean onCreate() {
        mBookmarkDBAccess = new BookmarkDBAccess(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        switch (sUriMatcher.match(uri)) {
            case IDX_MIME_ITEM:
                final long bookmark_id = Long.parseLong(uri.getLastPathSegment());
                Bookmark bm = mBookmarkDBAccess.findById(bookmark_id);
                if (values.containsKey(Bookmark.Columns.NAME)) {
                    bm.setName(values.getAsString(Bookmark.Columns.NAME));
                }
                if (values.containsKey(Bookmark.Columns.URL)) {
                    bm.setUrl(values.getAsString(Bookmark.Columns.URL));
                }
                count = mBookmarkDBAccess.update(bm);
                break;

            case IDX_MIME_DIR:
                count = mBookmarkDBAccess.update(values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
