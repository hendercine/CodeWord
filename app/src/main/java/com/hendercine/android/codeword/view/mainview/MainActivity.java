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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hendercine.android.codeword.R;
import com.hendercine.android.codeword.data.WordClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String CODE_WORD = "codeWord";
    private static final String GUESS_STRING = "guessString";
    private static final String CORRECT_COUNT = "correctCount";
    private static final String ENTERED_LETTERS = "enteredLetters";

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
    private ArrayList<Character> mEnteredLetters;
    private StringBuilder mGuessedLettersBuilder;
    private ArrayList<String> mCodeWordsList;
    @SuppressWarnings("unused")
    private Subscription mSubscription;
    private Editable mUserInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mEnteredLetters = new ArrayList<>();
        mCount = 6;
        mCorrectCount = 0;
        setBombImage(mCount);
        getCodeWordsListFromApi();
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
    protected void onSaveInstanceState(Bundle outState) {
        // TODO: refactor to allow landscape mode and saved instances
        outState.putInt(TRIES_COUNT, mCount);
        outState.putInt(CORRECT_COUNT, mCorrectCount);
        outState.putString(GUESS_STRING, mGuessStr);
        outState.putString(CODE_WORD, mCodeWord);
//        outState.putCharArray(ENTERED_LETTERS, mEnteredLetters);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
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
                        mCodeWord = mCodeWordsList.get(new Random().nextInt(mCodeWordsList
                                .size()));
                        mEnteredLetters = new ArrayList<>();
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
        if (mEnteredLetters.contains(guessLetter)) {
            mUserInput.clear();
            return;
        }
        mEnteredLetters.add(guessLetter);
        for (int i = 0; i < codeWord.length(); i++) {
            // Check for correctness
            if (codeWord.charAt(i) == guessLetter) {
                revealCorrectPositions(i);
                correctGuess = true;
            }
        }
        // Check for victory conditions
        if (mCorrectCount == mCodeWord.length()) {
            Toast.makeText(
                    this,
                    R.string.you_win,
                    Toast.LENGTH_LONG
            )
                    .show();

        }
        // Check for game end conditions
        if (!correctGuess && mCount > 1) {
            // Incorrect but still have guesses left
            setBombImage(mCount - 1);
            mGuessedLetters.setText(mGuessedLettersBuilder.append(guessLetter));
            mCount--;
        } else if (!correctGuess && mCount == 1) {
            // Set game lost conditions
            setBombImage(mCount - 1);
            Toast.makeText(
                    this,
                    R.string.you_lose,
                    Toast.LENGTH_LONG
            ).show();
            revealCorrectPositions(6);
            mCount = 6;
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
        if (mUserInput != null) {
            mUserInput.clear();
        }
        mGuessInput.clearFocus();
        MainActivity.this.recreate();
    }
}
