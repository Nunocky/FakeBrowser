package org.nunocky.fakebrowser;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

import org.nunocky.fakebrowser.bookmark.Bookmark;
import org.nunocky.fakebrowser.bookmark.BookmarkDBAccess;
import org.nunocky.fakebrowser.bookmark.BookmarkProvider;

public class BookmarkProviderTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private static final String TAG = "BookmarkProviderTest";

    String[] TEST_NAMES;
    String[] TEST_URLS;

    private ContentResolver mContentResolver;

//    @Override
//    protected void scrubClass(Class testCaseClass) {
//        // ignore
//    }


    void setupDB() {
        BookmarkDBAccess access = new BookmarkDBAccess(getActivity());
        access.deleteAll();

        for (int n = 0; n < TEST_NAMES.length; n++) {
            access.create(TEST_NAMES[n], TEST_URLS[n]);
        }
        access.close();
    }

    private void deleteAllElemets() {
        BookmarkDBAccess access = new BookmarkDBAccess(getActivity());
        access.deleteAll();
    }

    Cursor findAll() {
        return mContentResolver.query(
                BookmarkProvider.CONTENT_URI,
                new String[]{Bookmark.Columns._ID, Bookmark.Columns.NAME, Bookmark.Columns.URL},
                null,
                null,
                Bookmark.Columns._ID + " ASC");
    }

    public BookmarkProviderTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TEST_NAMES = new String[]{"A", "B", "C", "D", "E"};
        TEST_URLS = new String[]{"http://a.com/", "http://b.com/", "http://c.com/", "http://d.com/", "http://e.com/"};
        setupDB();
        mContentResolver = getActivity().getContentResolver();
    }

    @Override
    protected void tearDown() throws Exception {
        deleteAllElemets();
        super.tearDown();
    }

    public void test_00_deleteAll() {
        int count = mContentResolver.delete(
                BookmarkProvider.CONTENT_URI,
                null,
                null);
    }

    public void test01_create() {
        ContentValues values = new ContentValues();
        values.put(Bookmark.Columns.NAME, "Google");
        values.put(Bookmark.Columns.URL, "http://www.google.com/");

        Uri uri = getActivity().getContentResolver().insert(
                BookmarkProvider.CONTENT_URI,
                values);

        assertNotNull(uri);
    }

    public void test02_query() {
        Cursor c = findAll();

        assertNotNull(c);
        assertEquals(TEST_NAMES.length, c.getCount());
    }

    public void test03_query1() {
        Cursor c;

        c = mContentResolver.query(
                BookmarkProvider.CONTENT_URI,
                new String[]{Bookmark.Columns._ID, Bookmark.Columns.NAME, Bookmark.Columns.URL},
                null,
                null,
                Bookmark.Columns._ID + " ASC");

        final int idx_name = c.getColumnIndex(Bookmark.Columns.NAME);
        final int idx_url = c.getColumnIndex(Bookmark.Columns.URL);

        int n = 0;
        int lastidx = 0;

        while (c.moveToNext()) {
            assertEquals(TEST_NAMES[n], c.getString(idx_name));
            assertEquals(TEST_URLS[n], c.getString(idx_url));
            n++;
            if (c.isLast())
                lastidx = c.getInt(c.getColumnIndex(Bookmark.Columns._ID));
        }

        // ----------------------------
        c = mContentResolver.query(
                Uri.parse(BookmarkProvider.CONTENT_URI + "/" + lastidx),
                null,
                null,
                null,
                null);

        assertNotNull(c);
        assertEquals(1, c.getCount());
    }

    public void test04_delete() {
        Cursor c = findAll();

        int total = c.getCount();

        while (c.getCount() > 0) {
            c.moveToLast();
            int lastidx = c.getInt(c.getColumnIndex(Bookmark.Columns._ID));

            int count = mContentResolver.delete(
                    Uri.parse(BookmarkProvider.CONTENT_URI + "/" + lastidx),
                    null,
                    null);
            assertEquals(1, count);
            total--;

            c = findAll();
        }

        assertEquals(0, total);
    }

    public void test05_update() {
        Cursor c = findAll();
        final int idx_id = c.getColumnIndex(Bookmark.Columns._ID);
        final int idx_name = c.getColumnIndex(Bookmark.Columns.NAME);
        final int idx_url = c.getColumnIndex(Bookmark.Columns.URL);

        c.moveToLast();

        ContentValues values = new ContentValues();
        values.put(Bookmark.Columns.NAME, "TEST00000");
        values.put(Bookmark.Columns.URL, "http://example.com.21211");

        mContentResolver.update(
                Uri.parse(BookmarkProvider.CONTENT_URI + "/" + c.getInt(idx_id)),
                values,
                null,
                null);
    }


}
