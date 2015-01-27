package org.nunocky.fakebrowser;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;

import org.nunocky.fakebrowser.bookmark.Bookmark;
import org.nunocky.fakebrowser.bookmark.BookmarkDBAccess;

public class DatabaseTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private static final String TAG = "DatabaseTest";
    private BookmarkDBAccess access;

    public DatabaseTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        access = new BookmarkDBAccess(getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        access.close();
        super.tearDown();
    }

//    public void test00_initDB() throws Exception {
//        access.deleteAll();
//    }

    public void test01_delete_all() throws Exception {
        access.deleteAll();
        Cursor c = access.queryAll();
        assertEquals(0, c.getCount());
    }

    public void test02_create() throws Exception {
        access.deleteAll();

        Bookmark bm = access.create("Google", "http://www.google.com/");

        Cursor c = access.queryAll();
        assertEquals(1, c.getCount());
    }

    public void test03_props() throws Exception {
        access.deleteAll();

        Bookmark bm = access.create("Google", "http://www.google.com/");

        assertNotNull(bm);
        assertEquals("Google", bm.getName());
        assertEquals("http://www.google.com/", bm.getUrl());
    }

    public void test05_update() throws Exception {
        access.deleteAll();

        Bookmark bm = access.create("Google", "http://www.google.com/");

        assertNotNull(bm);

        bm.setName("Google2");
        access.update(bm);

        bm = access.findByName("Google2");
        assertNotNull(bm);
        assertEquals("Google2", bm.getName());
    }

    public void test06_update_props() throws Exception {
        access.deleteAll();

        Bookmark bm = access.create("Google", "http://www.google.com/");
        assertNotNull(bm);

        access.update(bm.getId(), "Google2", bm.getUrl());

        bm = access.findByName("Google2");
        assertNotNull(bm);
    }

    public void test07_delete() throws Exception {
        access.deleteAll();

        Bookmark bm = access.create("Google", "http://www.google.com/");

        access.delete(bm.getId());

        Cursor c = access.queryAll();
        assertEquals(0, c.getCount());
    }

    public void test08_delete_bm() throws Exception {
        access.deleteAll();

        Bookmark bm = access.create("Google", "http://www.google.com/");

        access.delete(bm);

        Cursor c = access.queryAll();
        assertEquals(0, c.getCount());
    }

}
