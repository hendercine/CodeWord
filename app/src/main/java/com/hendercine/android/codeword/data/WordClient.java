/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/24/18 4:50 PM
 */

package com.hendercine.android.codeword.data;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
/**
 * codeword created by James Henderson on 11/24/18.
 */
public class WordClient {

    private static final String BASE_URL = "http://app.linkedin-reach.io/";

    private static WordClient instance;
    private static WordApiService mWordApiService;

    public WordClient() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mWordApiService = retrofit.create(WordApiService.class);
    }

    public static WordClient getInstance() {
        if (instance == null) {
            instance = new WordClient();
        }
        return instance;
    }

    public Observable<String> getWordsFromApi(int difficulty, int minLength,
                                              int maxLength) {
        return mWordApiService.getWordsText(difficulty, minLength, maxLength);
    }
}
