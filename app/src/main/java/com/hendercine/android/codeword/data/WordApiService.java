/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/24/18 4:50 PM
 */

package com.hendercine.android.codeword.data;

import java.util.ArrayList;

import retrofit2.http.GET;
import rx.Observable;
/**
 * codeword created by James Henderson on 11/24/18.
 */
public interface WordApiService {

    @GET("words")
    Observable<ArrayList<String>> getWordsList();
}
