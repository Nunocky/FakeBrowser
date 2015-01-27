package org.nunocky.fakebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import org.nunocky.fakebrowser.event.CreateBookmarkEvent;
import org.nunocky.fakebrowser.event.EditBookmarkEvent;

import de.greenrobot.event.EventBus;


public class BookmarkActivity extends ActionBarActivity {

    private static final String FTAG = "listfragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookmarkListFragment fragment = (BookmarkListFragment) getSupportFragmentManager().findFragmentByTag(FTAG);
        if (fragment == null) {
            fragment = new BookmarkListFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content, fragment, FTAG);
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

    public void moveToCreateActivity() {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    public void moveToEditActivity(long item_id) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("item_id", item_id);
        startActivity(intent);
    }

    public void onEvent(CreateBookmarkEvent ev) {
        moveToCreateActivity();
    }

    public void onEvent(EditBookmarkEvent ev) {
        moveToEditActivity(ev.getId());
    }
}
