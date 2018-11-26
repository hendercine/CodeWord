/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/24/18 4:50 PM
 */

package com.hendercine.android.codeword.data;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
/**
 * codeword created by James Henderson on 11/24/18.
 */
public interface WordApiService {

    @GET("words")
    Observable<String> getWordsText(@Query("difficulty") int difficulty,
                                    @Query("minLength") int minLength,
                                    @Query("maxLength") int maxLength);
}
