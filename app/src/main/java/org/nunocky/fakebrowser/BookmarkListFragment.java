package org.nunocky.fakebrowser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.nunocky.fakebrowser.bookmark.Bookmark;
import org.nunocky.fakebrowser.bookmark.BookmarkProvider;
import org.nunocky.fakebrowser.event.CreateBookmarkEvent;
import org.nunocky.fakebrowser.event.EditBookmarkEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by nunokawa on 15/01/22.
 */
public class BookmarkListFragment extends ListFragment {
    private static final String TAG = "BookmarkListFragment";

    private SimpleCursorAdapter mAdapter;

    private Cursor mCursor;


    LoaderManager.LoaderCallbacks<Cursor> mCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(
                    getActivity(),
                    BookmarkProvider.CONTENT_URI,
                    null, // new String[]{Bookmark.Columns._ID, Bookmark.Columns.NAME, Bookmark.Columns.URL},
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            Cursor oldCursor = mCursor;
            mCursor = cursor;

            if (oldCursor != null && oldCursor != mCursor) {
                oldCursor.close();
            }

            mAdapter.swapCursor(mCursor);
            setListShown(true);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            if (mCursor != null) {
                mCursor.close();
            }
            mCursor = null;
            mAdapter.swapCursor(null);
        }
    };

    public BookmarkListFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, mCallbacks);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[]{Bookmark.Columns.NAME, Bookmark.Columns.URL},
                new int[]{android.R.id.text1, android.R.id.text2},
                0);
        setListAdapter(mAdapter);

        setListShown(false);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            EventBus.getDefault().post(new CreateBookmarkEvent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor c = (Cursor) mAdapter.getItem(position);
        final int idx_id = c.getColumnIndex(Bookmark.Columns._ID);
        // final int idx_name = c.getColumnIndex(Bookmark.Columns.NAME);
        // final int idx_url = c.getColumnIndex(Bookmark.Columns.URL);

        EventBus.getDefault().post(new EditBookmarkEvent(c.getInt(idx_id)));
    }


}
