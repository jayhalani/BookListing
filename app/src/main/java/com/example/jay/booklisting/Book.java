package com.example.jay.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jay on 4/4/2017.
 */

public class Book implements Parcelable {

    private String mBookTitle;

    private String mBookAuthors;

    private String mUrl;


    public Book( String bookTitle, String bookAuthor, String url) {
        mBookAuthors = bookAuthor;
        mBookTitle = bookTitle;
        mUrl = url;
    }

    private Book(Parcel in) {
        mBookAuthors = in.readString();
        mBookTitle = in.readString();
        mUrl = in.readString();
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getBookAuthor() {
        return mBookAuthors;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBookTitle);
        dest.writeString(mBookAuthors);
        dest.writeString(mUrl);
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
