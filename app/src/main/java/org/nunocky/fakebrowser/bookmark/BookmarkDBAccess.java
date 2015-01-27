package org.nunocky.fakebrowser.bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

public class BookmarkDBAccess {
    private static final String DATABASE_NAME = "bookmark.db";
    private static final String DATABASE_TABLE = "bookmarks";

    private static class DBHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;

        DBHelper(Context cxt) {
            super(cxt, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format(
                    "create table %s (%s integer primary key autoincrement, %s text not null, %s text not null);",
                    DATABASE_TABLE,
                    Bookmark.Columns._ID,
                    Bookmark.Columns.NAME,
                    Bookmark.Columns.URL);

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // MEMO : 適切なマイグレーション処理を実装する
            String sql = String.format(
                    "drop table if exists %s",
                    DATABASE_TABLE);
            db.execSQL(sql);
            onCreate(db);
        }
    }

    //    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;

    public BookmarkDBAccess(Context cxt) {
        DBHelper mHelper;
        mHelper = new DBHelper(cxt);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }

//        if (mHelper != null) {
//            mHelper.close();
//        }

        mDatabase = null;
        //mHelper = null;
    }

    public Bookmark create(String name, String url) {
        ContentValues values = new ContentValues();
        values.put(Bookmark.Columns.NAME, name);
        values.put(Bookmark.Columns.URL, url);

        long rowID = mDatabase.insert(DATABASE_TABLE, "", values);
        if (rowID > 0) {
            return new Bookmark(rowID, name, url);
//            return rowID;
        }

        throw new SQLiteException("insert failed");
    }

    public Cursor queryAll() {
        String[] projection = {Bookmark.Columns._ID, Bookmark.Columns.NAME, Bookmark.Columns.URL};
        return this.query(projection, null, null, null);
    }

//    public Cursor find(long from, int count) {
//        return null;
//    }

    /**
     * @param id
     * @return
     */
    Cursor queryById(long id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DATABASE_TABLE);

        String[] projection = {Bookmark.Columns._ID, Bookmark.Columns.NAME, Bookmark.Columns.URL};
        String selection = Bookmark.Columns._ID + "=?";
        String[] selectionArgs = {Long.toString(id)};
        String sortOrder = null;

        return this.query(projection, selection, selectionArgs, sortOrder);
    }

    public Bookmark findById(long id) {
        Cursor c = queryById(id);

        Bookmark bm;
        do {
            c.moveToNext();
            bm = new Bookmark(c.getInt(0), c.getString(1), c.getString(2));
        } while (false);
        return bm;
    }


    Cursor queryByName(String name) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DATABASE_TABLE);

        String[] projection = {Bookmark.Columns._ID, Bookmark.Columns.NAME, Bookmark.Columns.URL};
        String selection = Bookmark.Columns.NAME + "=?";
        String[] selectionArgs = {name};
        String sortOrder = null;

        return this.query(projection, selection, selectionArgs, sortOrder);
    }

    public Bookmark findByName(String name) {
        Cursor c = queryByName(name);
        Bookmark bm;

        do {
            c.moveToNext();
            bm = new Bookmark(c.getInt(0), c.getString(1), c.getString(2));
        } while (false);
        return bm;
    }

    /**
     * id番の要素を更新する
     *
     * @param id   変更対象のID
     * @param name
     * @param url
     * @return 更新されたデータの個数
     */
    public int update(long id, String name, String url) {

        if (name == null || url == null) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(Bookmark.Columns.NAME, name);
        values.put(Bookmark.Columns.URL, url);

        if (values.size() == 0) {
            return 0;
        }

        int count = mDatabase.update(
                DATABASE_TABLE,
                values,
                String.format("%s=?", Bookmark.Columns._ID),
                new String[]{Long.toString(id)});

        return count;
    }

    /**
     * @param bm
     * @return
     */
    public int update(final Bookmark bm) {
        if (bm == null)
            return 0;
        return update(bm.getId(), bm.getName(), bm.getUrl());
    }

    /**
     * id番の要素を削除する
     *
     * @param id
     * @return 削除されたデータの個数
     */
    public int delete(long id) {
        int count = mDatabase.delete(
                DATABASE_TABLE,
                String.format("%s=?", Bookmark.Columns._ID),
                new String[]{Long.toString(id)});
        return count;
    }

    /**
     * @param bm
     * @return
     */
    public int delete(final Bookmark bm) {
        if (bm == null) {
            return 0;
        }
        return delete(bm.getId());
    }

    /**
     * 全アイテムの削除
     */
    public void deleteAll() {
        String sql = String.format("delete from " + DATABASE_TABLE);
        mDatabase.execSQL(sql);
    }


    /**
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DATABASE_TABLE);

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = Bookmark.Columns._ID + " ASC";
        }

        Cursor c = builder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        return c;
    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        return mDatabase.update(
                DATABASE_TABLE,
                values,
                selection,
                selectionArgs);
    }

    public int delete(String selection, String[] selectionArgs) {
        return mDatabase.delete(
                DATABASE_TABLE,
                selection,
                selectionArgs);
    }


}
