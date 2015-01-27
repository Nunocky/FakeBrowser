package org.nunocky.fakebrowser;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.nunocky.fakebrowser.bookmark.Bookmark;
import org.nunocky.fakebrowser.bookmark.BookmarkProvider;
import org.nunocky.fakebrowser.event.DeleteItemEvent;
import org.nunocky.fakebrowser.event.UpdateBookmarkEvent;

import de.greenrobot.event.EventBus;


public class EditActivity extends ActionBarActivity {

    private static final String FTAG = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));

        EditorFragment fragment = (EditorFragment) getSupportFragmentManager().findFragmentByTag(FTAG);
        if (fragment == null) {
            Bundle args = new Bundle();

            Intent intent = getIntent();
            long item_id = intent.getLongExtra("item_id", -1);
            args.putLong("item_id", item_id);

            fragment = new EditorFragment();
            fragment.setArguments(args);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment, FTAG);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(UpdateBookmarkEvent ev) {
        updateItem(ev.getId(), ev.getName(), ev.getUrl());
        finish();
    }

    private void updateItem(long id, String name, String url) {
        ContentResolver mContentResolver = getContentResolver();

        ContentValues values = new ContentValues();
        values.put(Bookmark.Columns.NAME, name);
        values.put(Bookmark.Columns.URL, url);

        mContentResolver.update(
                Uri.parse(BookmarkProvider.CONTENT_URI + "/" + id),
                values,
                null,
                null);

    }

    public void onEvent(DeleteItemEvent ev) {
        deleteItem(ev.getId());
        finish();
    }

    private void deleteItem(long id) {
        ContentResolver mContentResolver = getContentResolver();

        mContentResolver.delete(
                Uri.parse(BookmarkProvider.CONTENT_URI + "/" + id),
                null,
                null);


    }

    public static class EditorFragment extends Fragment {
        private long mItemId;
        private Button button;
        private EditText et0, et1;

        public EditorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_create, container, false);

            button = (Button) v.findViewById(R.id.button);
            et0 = (EditText) v.findViewById(R.id.editText);
            et1 = (EditText) v.findViewById(R.id.editText2);

            button.setText(R.string.action_update);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditorFragment.this.onButtonClicked(v);
                }
            });

            mItemId = getArguments().getLong("item_id");
            Cursor c = getActivity().getContentResolver().query(
                    Uri.parse(BookmarkProvider.CONTENT_URI + "/" + mItemId),
                    null,
                    null,
                    null,
                    null);

            if (c.moveToNext()) {
                int idx_name = c.getColumnIndex(Bookmark.Columns.NAME);
                int idx_url = c.getColumnIndex(Bookmark.Columns.URL);
                et0.setText(c.getString(idx_name));
                et1.setText(c.getString(idx_url));
            }

            return v;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setHasOptionsMenu(true);
        }

        private void onButtonClicked(View v) {
            String name = et0.getText().toString();
            String url = et1.getText().toString();

            if (name == null || url == null)
                return;

            EventBus.getDefault().post(new UpdateBookmarkEvent(mItemId, name, url));
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            MenuItem item = menu.add(0, 0, 0, "delete");
            item.setIcon(android.R.drawable.ic_menu_delete);

            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == 0) {
                EventBus.getDefault().post(new DeleteItemEvent(mItemId));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
