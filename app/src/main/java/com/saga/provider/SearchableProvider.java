package com.saga.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Bruno Nogueira Silva on 12/28/15.
 */
public class SearchableProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.saga.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider(){
        setupSuggestions( AUTHORITY, MODE );
    }
}
