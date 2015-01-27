package org.nunocky.fakebrowser;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.nunocky.fakebrowser.bookmark.Bookmark;
import org.nunocky.fakebrowser.bookmark.BookmarkProvider;

/**
 * Created by nunokawa on 15/01/22.
 */
public class CreateActivity extends ActionBarActivity {
    private static final String TAG = "CreateActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));

        CreateFragment fragment = (CreateFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new CreateFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment, "fragment");
            ft.commit();
        }
    }

    public static class CreateFragment extends Fragment {
        private Button button;
        private EditText et0, et1;

        public CreateFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_create, container, false);

            button = (Button) v.findViewById(R.id.button);
            et0 = (EditText) v.findViewById(R.id.editText);
            et1 = (EditText) v.findViewById(R.id.editText2);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateFragment.this.onButtonClicked(v);
                }
            });
            return v;
        }

        private void onButtonClicked(View v) {
            // TODO : add new entry
            ContentResolver mContentResolver = getActivity().getContentResolver();

            ContentValues values = new ContentValues();
            values.put(Bookmark.Columns.NAME, et0.getText().toString());
            values.put(Bookmark.Columns.URL, et1.getText().toString());

            mContentResolver.insert(
                    BookmarkProvider.CONTENT_URI,
                    values
            );
            getActivity().finish();
        }

    }

}
