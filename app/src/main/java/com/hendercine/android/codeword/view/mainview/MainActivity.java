/*
 * Created by James Henderson on 2018
 * Copyright (c) Hendercine Productions and James Henderson 2018.
 * All rights reserved.
 *
 * Last modified 11/24/18 2:43 PM
 */

package com.hendercine.android.codeword.view.mainview;

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

import com.bumptech.glide.Glide;
import com.hendercine.android.codeword.R;
import com.hendercine.android.codeword.data.WordClient;
import com.hendercine.android.codeword.view.base.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private static final String TRIES_COUNT = "triesCount";
    private static final String CODE_WORD = "codeWord";
    private static final String GUESS_STRING = "guessString";
    private static final String CORRECT_COUNT = "correctCount";
    private static final String ENTERED_LETTERS = "enteredLetters";
    private static final String LETTER_1 = "letter_1";
    private static final String LETTER_2 = "letter_2";
    private static final String LETTER_3 = "letter_3";
    private static final String LETTER_4 = "letter_4";
    private static final String LETTER_5 = "letter_5";
    private static final String LETTER_6 = "letter_6";

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
    @SuppressWarnings("unused")
    private Subscription mSubscription;
    private Editable mUserInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mCount = 6;
            mCorrectCount = 0;
            getCodeWordsListFromApi();
        } else {
            mCount = savedInstanceState.getInt(TRIES_COUNT);
            mCorrectCount = savedInstanceState.getInt(CORRECT_COUNT);
            mGuessStr = savedInstanceState.getString(GUESS_STRING);
            mCodeWord = savedInstanceState.getString(CODE_WORD);
            mEnteredLetters = savedInstanceState.getCharArray(ENTERED_LETTERS);
        }

        if (savedInstanceState != null) {
            boolean visibilityOne = savedInstanceState.getBoolean(LETTER_1);
        }

        setBombImage(mCount);

        // Maintain below commented code for debugging
//        mCodeWordsList = new ArrayList<>();
//        mCodeWordsList.addAll(Arrays.asList("linked", "inmail", "street"));
//        mCodeWord = mCodeWordsList.get(new Random().nextInt(mCodeWordsList.size()));

        mGuessInput.setActivated(true);
        mGuessedLettersBuilder = new StringBuilder();
        mGuessedLetters.setText("");
        hideKeyboardOnKeyTouch(mGuessInput);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TRIES_COUNT, mCount);
        outState.putString(CODE_WORD, mCodeWord);
        outState.putString(GUESS_STRING, mGuessStr);
        outState.putInt(CORRECT_COUNT, mCorrectCount);
        outState.putCharArray(ENTERED_LETTERS,mEnteredLetters);
        outState.putBoolean(LETTER_1, hasVisibleLetter(mLetterOne));
        outState.putBoolean(LETTER_2, hasVisibleLetter(mLetterTwo));
        outState.putBoolean(LETTER_3, hasVisibleLetter(mLetterThree));
        outState.putBoolean(LETTER_4, hasVisibleLetter(mLetterFour));
        outState.putBoolean(LETTER_5, hasVisibleLetter(mLetterFive));
        outState.putBoolean(LETTER_6, hasVisibleLetter(mLetterSix));
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
            showToast(R.string.you_win);
        }
        // Check for game end conditions
        if (!correctGuess && mCount > 1) {
            setBombImage(mCount - 1);
            mEnteredLetters = String.valueOf(guessLetter).toCharArray();
            addGuessedLettersToView();
            mCount--;
        } else if (!correctGuess && mCount == 1) {
            // Set game lost conditions
            setBombImage(mCount - 1);
            showToast(R.string.you_lose);
            revealCorrectPositions(6);
            mCount = 6;
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
                glideHelper(R.drawable.explosion_giphy);
                break;
            case 1:
                glideHelper(R.drawable.last_guess);
                break;
            case 2:
                glideHelper(R.drawable.two_more_guesses);
                break;
            case 3:
                glideHelper(R.drawable.three_left);
                break;
            case 4:
                glideHelper(R.drawable.fourth_bomb);
                break;
            case 5:
                glideHelper(R.drawable.t_minus_five);
                break;
            case 6:
                glideHelper(R.drawable.first_guess);
                break;
        }
    }

    private void glideHelper(int imgRes) {
        Glide.with(MainActivity.this)
                .load(imgRes)
                .into(mCountdown);
    }

    private void setTextInPosition(@NotNull TextView textView, int caseCount) {
        if (caseCount >= 0 && caseCount < 7) {
            textView.setText(String.valueOf(mCodeWord.charAt(caseCount))
                    .toUpperCase());
            textView.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasVisibleLetter(TextView letterView) {
        return letterView.getVisibility() == View.VISIBLE;
    }

    public static void hideKeyboard(@NotNull Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideKeyboardOnKeyTouch(final EditText editText) {

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (editText.getText().toString().length() == 1) {
                    hideKeyboard(MainActivity.this, mGuessInput);
                }
                return true;
            }
        });
    }

    public void resetGame() {
        mUserInput.clear();
        mGuessInput.clearFocus();
        mCount = 6;
        mCorrectCount = 0;
        getCodeWordsListFromApi();
        MainActivity.this.recreate();
    }
}
