/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/27/18 10:29 AM
 */

package com.hendercine.android.codeword.view.base;

import android.arch.core.BuildConfig;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * codeword created by James Henderson on 11/27/18.
 */
public abstract class BaseActivity extends AppCompatActivity {


    public Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(getActivityLayout());
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract int getActivityLayout();

    public void glideHelper(int imgRes, AppCompatImageView compatImageView) {
        Glide.with(getApplicationContext())
                .load(imgRes)
                .into(compatImageView);
    }

    public void setUnbinder(Unbinder unbinder) {
        mUnbinder = unbinder;
    }

    public void showSnackBar(int resId) {
        Snackbar.make(
                findViewById(Objects.requireNonNull(getCurrentFocus()).getId()),
                resId,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId) {
        Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
