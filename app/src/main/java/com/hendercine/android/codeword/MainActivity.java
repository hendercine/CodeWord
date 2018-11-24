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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TRIES_COUNT = "triesCount";

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

    private String mGuessStr;
    private String mCodeWord;
    private int mCount;
    private int mCorrectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mCount = 6;
            mCorrectCount = 0;
        } else {
            mCount = savedInstanceState.getInt(TRIES_COUNT);
        }
        mCountdown.setText(String.valueOf(mCount));
        mCodeWord = "Street";
        mGuessInput.setActivated(true);
        mGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable userInput = mGuessInput.getText();
                mGuessStr = userInput.toString();
                if (mGuessStr.length() != 0) {
                    checkGuess(
                            String.valueOf(mGuessStr).charAt(0),
                            mCodeWord.toUpperCase()
                    );
                    userInput.clear();
                    mGuessInput.clearFocus();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TRIES_COUNT, mCount);
        super.onSaveInstanceState(outState);
    }

    private void checkGuess(char guessLetter, String codeWord) {

        boolean correctGuess = false;
        for (int i = 0; i < codeWord.length(); i++) {
            if (codeWord.charAt(i) == guessLetter) {
                revealCorrectPositions(i);
                correctGuess = true;
            }
        }
        // Check for victory conditions
        if (mCorrectCount == mCodeWord.length()) {
            Toast.makeText(this,
                    "YOU HAVE DISCOVERED THE CODE WORD!!\nYOU WIN!!",
                    Toast.LENGTH_LONG)
                    .show();

            resetGame();
        }
        // Check for game end conditions
        if (!correctGuess && mCount > 1) {
            mCountdown.setText(String.valueOf(mCount - 1));
            mCount--;
        } else if (!correctGuess && mCount == 1){
            mCountdown.setText(String.valueOf(mCount - 1));
            Toast.makeText(this,
                    "NO MORE GUESSES\nGAME OVER",
                    Toast.LENGTH_LONG)
                    .show();
            revealCorrectPositions(6);

            resetGame();
        }
    }

    private void revealCorrectPositions(int correctGuessPositions) {

        switch (correctGuessPositions) {
            case 0:
                mLetterOne.setText(String.valueOf(mCodeWord.charAt(0)).toUpperCase());
                mLetterOne.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 1:
                mLetterTwo.setText(String.valueOf(mCodeWord.charAt(1)).toUpperCase());
                mLetterTwo.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 2:
                mLetterThree.setText(String.valueOf(mCodeWord.charAt(2)).toUpperCase());
                mLetterThree.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 3:
                mLetterFour.setText(String.valueOf(mCodeWord.charAt(3)).toUpperCase());
                mLetterFour.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 4:
                mLetterFive.setText(String.valueOf(mCodeWord.charAt(4)).toUpperCase());
                mLetterFive.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 5:
                mLetterSix.setText(String.valueOf(mCodeWord.charAt(5)).toUpperCase());
                mLetterSix.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 6:
                mLetterOne.setText(String.valueOf(mCodeWord.charAt(0)).toUpperCase());
                mLetterOne.setVisibility(View.VISIBLE);
                mLetterTwo.setText(String.valueOf(mCodeWord.charAt(1)).toUpperCase());
                mLetterTwo.setVisibility(View.VISIBLE);
                mLetterThree.setText(String.valueOf(mCodeWord.charAt(2)).toUpperCase());
                mLetterThree.setVisibility(View.VISIBLE);
                mLetterFour.setText(String.valueOf(mCodeWord.charAt(3)).toUpperCase());
                mLetterFour.setVisibility(View.VISIBLE);
                mLetterFive.setText(String.valueOf(mCodeWord.charAt(4)).toUpperCase());
                mLetterFive.setVisibility(View.VISIBLE);
                mLetterSix.setText(String.valueOf(mCodeWord.charAt(5)).toUpperCase());
                mLetterSix.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void resetGame() {
        MainActivity.this.recreate();
    }
}
