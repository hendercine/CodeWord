/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/18/18 10:29 PM
 */

package com.hendercine.android.codeword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.guess_edit_text)
    EditText mGuessInput;
    @BindView(R.id.guess_button)
    Button mGuessButton;
    @BindView(R.id.letter_1)
    TextView mLetterOne;
    @BindView(R.id.letter_2)
    TextView mLetterTwo;
    @BindView(R.id.letter_3)
    TextView mLetterThree;
    @BindView(R.id.letter_4)
    TextView mLetterFour;
    @BindView(R.id.letter_5)
    TextView mLetterFive;
    @BindView(R.id.letter_6)
    TextView mLetterSix;
    @BindView(R.id.countdown_view)
    TextView mCountdown;

    private Editable mGuessStr;
    private String mCodeWord = "Linked";
    private int mCount;
    private List<Integer> mCorrectGuessPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mCount = 6;
        }
        mCountdown.setText(String.valueOf(mCount));
        mGuessInput.setActivated(true);
        mGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuessStr = mGuessInput.getText();
                ArrayList<Integer> positions = (ArrayList<Integer>) checkGuess(mGuessStr
                        .charAt(0));
                if (positions != null) {
                    revealCorrectPositions(positions);
                }
            }
        });
    }

    private List<Integer> checkGuess(char guessLetter) {

        mCorrectGuessPositions = new ArrayList<>();
        for (int i = 0; i < mCodeWord.length(); i++) {
            if (mCodeWord.charAt(i) == guessLetter) {
                mCorrectGuessPositions.add(i);
            }
        }

        return mCorrectGuessPositions;
    }

    private void revealCorrectPositions(ArrayList<Integer>
                                                correctGuessPositions) {

        for (int i = 0; i < correctGuessPositions.size(); i++) {
            if (correctGuessPositions.get(i) == 0) {
                mLetterOne.setText(mCodeWord.charAt(0));
                mLetterOne.setVisibility(View.VISIBLE);
            } else if (correctGuessPositions.get(i) == 1) {
                mLetterTwo.setText(mCodeWord.charAt(1));
                mLetterTwo.setVisibility(View.VISIBLE);
            } else if (correctGuessPositions.get(i) == 2) {
                mLetterThree.setText(mCodeWord.charAt(2));
                mLetterThree.setVisibility(View.VISIBLE);
            } else if (correctGuessPositions.get(i) == 3) {
                mLetterFour.setText(mCodeWord.charAt(3));
                mLetterFour.setVisibility(View.VISIBLE);
            } else if (correctGuessPositions.get(i) == 4) {
                mLetterFive.setText(mCodeWord.charAt(4));
                mLetterFive.setVisibility(View.VISIBLE);
            } else if (correctGuessPositions.get(i) == 5) {
                mLetterSix.setText(mCodeWord.charAt(5));
                mLetterSix.setVisibility(View.VISIBLE);
            } else {
                mCountdown.setText(String.valueOf(mCount - 1));
            }
        }
    }
}
