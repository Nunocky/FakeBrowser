package org.nunocky.fakebrowser;

import android.content.Intent;
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
import android.widget.FrameLayout;

import org.nunocky.fakebrowser.event.GoToBookmarkEvent;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));
        WebViewFragment fragment = (WebViewFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new WebViewFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment, "fragment");
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

    public void onEvent(GoToBookmarkEvent ev) {
        Intent intent = new Intent(this, BookmarkActivity.class);
        startActivity(intent);
    }

    public static class WebViewFragment extends Fragment {

        public WebViewFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_web_view, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            MenuItem item = menu.add(0, 0, 0, "Bookmark");
            item.setIcon(android.R.drawable.ic_menu_gallery);

            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == 0) {
                EventBus.getDefault().post(new GoToBookmarkEvent());
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }

}
