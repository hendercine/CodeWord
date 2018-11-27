/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/24/18 2:43 PM
 */

package com.hendercine.android.codeword.view.mainview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hendercine.android.codeword.R;
import com.hendercine.android.codeword.data.WordClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String TRIES_COUNT = "triesCount";

    @BindView(R.id.guess_edit_text)
    EditText mGuessInput;
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
    AppCompatImageView mCountdown;
    @BindView(R.id.guessed_letters_view)
    TextView mGuessedLetters;

    private String mGuessStr;
    private String mCodeWord;
    private int mCount;
    private int mCorrectCount;
    private char[] mEnteredLetters;
    private StringBuilder mGuessedLettersBuilder;
    private ArrayList<String> mCodeWordsList;
    private Editable mUserInput;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mCount = 5;
            mCorrectCount = 0;
        } else {
            mCount = savedInstanceState.getInt(TRIES_COUNT);
        }
//        mCountdown.setText(String.valueOf(mCount));
        setBombImage(4);
//        getCodeWordsListFromApi();

        // Maintain below commented code for debugging
        mCodeWordsList = new ArrayList<>();
        mCodeWordsList.addAll(Arrays.asList("linked", "inmail", "street"));
        mCodeWord = mCodeWordsList.get(new Random().nextInt(mCodeWordsList.size()));
        mGuessInput.setActivated(true);
        mGuessedLettersBuilder = new StringBuilder();
        mGuessedLetters.setText("");
//        hideKeyboardOnKeyTouch(mGuessInput);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TRIES_COUNT, mCount);
        super.onSaveInstanceState(outState);
    }

    private void getCodeWordsListFromApi() {
        mSubscription = WordClient.getInstance()
                .getWordsFromApi(1, 6, 7)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("In onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("In onError");
                    }

                    @Override
                    public void onNext(String wordsText) {
                        Timber.d("In onNext");
                        mCodeWordsList = new ArrayList<>(Arrays
                                .asList(wordsText.split("\\s+")));
                        mCodeWord = mCodeWordsList.get(new Random().nextInt(mCodeWordsList.size()));
                        mEnteredLetters = new char[mCodeWord.length()];
                    }
                });

    }

    @OnClick(R.id.guess_button)
    void submit() {
        submitLetter();
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        resetGame();
    }

    private void submitLetter() {
        mUserInput = mGuessInput.getText();
        mGuessStr = mUserInput.toString();
        if (mGuessStr.length() != 0) {
            checkGuess(
                    String.valueOf(mGuessStr).charAt(0),
                    mCodeWord.toUpperCase()
            );
            mUserInput.clear();
            mGuessInput.clearFocus();
            hideKeyboard(MainActivity.this, mGuessInput);
        }
    }

    private void checkGuess(char guessLetter, @NotNull String codeWord) {

        boolean correctGuess = false;
        for (int i = 0; i < codeWord.length(); i++) {
            if (codeWord.charAt(i) == guessLetter) {
                revealCorrectPositions(i);
                correctGuess = true;
            }
        }
        // Check for victory conditions
        if (mCorrectCount == mCodeWord.length()) {
            Toast.makeText(
                    this,
                    "YOU HAVE DISCOVERED THE CODE WORD!!\nYOU WIN!!",
                    Toast.LENGTH_LONG
            )
                    .show();

//            resetGame();
        }
        // Check for game end conditions
        if (!correctGuess && mCount > 1) {
//            mCountdown.setText(String.valueOf(mCount - 1));
            setBombImage(mCount - 1);
            mEnteredLetters = String.valueOf(guessLetter).toCharArray();
            addGuessedLettersToView();
            mCount--;
        } else if (!correctGuess && mCount == 1) {
            // Set game lost conditions
//            mCountdown.setText(String.valueOf(mCount - 1));
            setBombImage(mCount - 1);
            Toast.makeText(
                    this,
                    "NO MORE GUESSES\nGAME OVER",
                    Toast.LENGTH_LONG
            ).show();
            revealCorrectPositions(6);
            mCount = 6;

//            resetGame();
        }
    }

    private void addGuessedLettersToView() {
        // Map entered chars
        Map<Character, Integer> charMap = new HashMap<>();
        for (char c : mEnteredLetters) {
            if (charMap.containsKey(c)) {
                int count = charMap.get(c);
                charMap.put(c, ++count);
            } else {
                charMap.put(c, 1);
            }
        }
        // Check mapped letters for duplicates and only display them once
        int charCount = 1;
        for (char c : charMap.keySet()) {
            if (charMap.get(c) == charCount) {
                mGuessedLetters.setText(mGuessedLettersBuilder
                        .append(c).toString());
                charCount++;
            }
        }
    }

    private void revealCorrectPositions(int correctGuessPosition) {

        switch (correctGuessPosition) {
            case 0:
                setTextInPosition(mLetterOne, correctGuessPosition);
                mCorrectCount++;
                break;
            case 1:
                setTextInPosition(mLetterTwo, correctGuessPosition);
                mCorrectCount++;
                break;
            case 2:
                setTextInPosition(mLetterThree, correctGuessPosition);
                mCorrectCount++;
                break;
            case 3:
                setTextInPosition(mLetterFour, correctGuessPosition);
                mCorrectCount++;
                break;
            case 4:
                setTextInPosition(mLetterFive, correctGuessPosition);
                mCorrectCount++;
                break;
            case 5:
                setTextInPosition(mLetterSix, correctGuessPosition);
                mCorrectCount++;
                break;
            case 6:
                setTextInPosition(mLetterOne, 0);
                setTextInPosition(mLetterTwo, 1);
                setTextInPosition(mLetterThree, 2);
                setTextInPosition(mLetterFour, 3);
                setTextInPosition(mLetterFive, 4);
                setTextInPosition(mLetterSix, 5);
                break;
        }
    }

    private void setBombImage(int count) {

        switch (count) {
            case 0:
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.explosion_giphy);
            case 1:
//                glideHelper(R.drawable.t_minus_1);
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.t_minus_1);
            case 2:
//                glideHelper(R.drawable.t_minus_2);
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.t_minus_2);
            case 3:
//                glideHelper(R.drawable.t_minus_3);
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.t_minus_3);
            case 4:
//                glideHelper(R.drawable.t_minus_4);
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.t_minus_4);
            case 5:
//                glideHelper(R.drawable.t_minus_5);
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.t_minus_5);
            case 6:
//                glideHelper(R.drawable.t_minus_6);
                mCountdown.setImageDrawable(null);
                mCountdown.setImageResource(R.drawable.t_minus_6);
        }
    }

    private void glideHelper(int imgRes) {
        mCountdown.setImageDrawable(null);
//        RequestOptions options = new RequestOptions()
//                .centerInside()
//                .placeholder(R.mipmap.ic_launcher_round)
//                .error(R.mipmap.ic_launcher_round);

//        Glide.with(MainActivity.this)
//                .load(imgRes)
////                .apply(options)
//                .into(mCountdown);
    }

    private void setTextInPosition(@NotNull TextView textView, int caseCount) {
        if (caseCount >= 0 && caseCount < 7) {
            textView.setText(String.valueOf(mCodeWord.charAt(caseCount))
                    .toUpperCase());
            textView.setVisibility(View.VISIBLE);
        }
    }

    public static void hideKeyboard(@NotNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideKeyboardOnKeyTouch(final EditText editText) {
//        final int generatedKeyCode = KeyEvent.keyCodeFromString
//                (editText.getText().toString());
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && editText.getText().toString().length() == 1) {
                    hideKeyboard(MainActivity.this, mGuessInput);
                }
                return true;
            }
        });
    }

    public void resetGame() {
        mCount = 6;
        MainActivity.this.recreate();
    }
}
