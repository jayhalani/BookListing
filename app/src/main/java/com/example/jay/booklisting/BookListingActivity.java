package com.example.jay.booklisting;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BookListingActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    BookAdapter mAdapter;
    EditText searchEditText;
    ListView booksListView;
    String searchQuery;
    TextView emptyTextView;

    ArrayList<Book> bookArrayList = new ArrayList<>();

    static final String BOOK_LIST_VALUES = "bookListValues";
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);
        if (savedInstanceState != null) {
            bookArrayList = savedInstanceState.getParcelableArrayList(BOOK_LIST_VALUES);
        }

        mAdapter = new BookAdapter(this, bookArrayList);
        booksListView = (ListView) findViewById(R.id.list_view);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        ;

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard(BookListingActivity.this, v);
                    return true;
                }
                return false;
            }
        });
        searchEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        emptyTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyTextView);

        booksListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });

        final Button searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(v);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(BOOK_LIST_VALUES, bookArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void performSearch(View v) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(BOOK_LOADER_ID, null, this);
        } else {
            emptyTextView.setText(R.string.no_internet_connection);
        }
        hideSoftKeyboard(BookListingActivity.this, v);
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        mAdapter.clear();
        searchQuery = searchEditText.getText().toString().replaceAll(" ", "+");
        return new BookLoader(this, BOOK_REQUEST_URL + searchQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else {
            emptyTextView.setText(R.string.no_books_found);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

}




