/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/28/18 12:11 PM
 */

package com.hendercine.android.codeword.view.gameover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.widget.TextView;

import com.hendercine.android.codeword.R;
import com.hendercine.android.codeword.view.base.BaseActivity;

import butterknife.BindView;

/**
 * CodeWord created by hendercine on 11/28/18.
 */
public class GameOverActivity extends BaseActivity {

    private static final String HAS_WON = "hasWon";

    @BindView(R.id.game_over_img)
    AppCompatImageView mGameOverImg;
    @BindView(R.id.win_or_lose_text_view)
    TextView mWinOrLoseView;

    private boolean mHasWon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        mHasWon = intent.getBooleanExtra(HAS_WON, false);
        if (mHasWon) {
            glideHelper(R.drawable.earth_day, mGameOverImg);
            mWinOrLoseView.setText(R.string.the_world_is_safe);
        } else {
            glideHelper(R.drawable.explosion_giphy, mGameOverImg);
            mWinOrLoseView.setText(R.string.you_lose);
        }


    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_game_over;
    }
}
