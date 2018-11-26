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
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hendercine.android.codeword.R;
import com.hendercine.android.codeword.data.WordClient;

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
    TextView mCountdown;
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
            mCount = 6;
            mCorrectCount = 0;
        } else {
            mCount = savedInstanceState.getInt(TRIES_COUNT);
        }
        mCountdown.setText(String.valueOf(mCount));
        getCodeWordsListFromApi();
//        mCodeWordsList = new ArrayList<>();
//        mCodeWordsList.addAll(Arrays.asList("linked", "inmail", "street"));
//        mCodeWord = mCodeWordsList.get(new Random().nextInt(mCodeWordsList.size()));
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
            mCountdown.setText(String.valueOf(mCount - 1));
            mEnteredLetters = String.valueOf(guessLetter).toCharArray();
            addGuessedLettersToView();
            mCount--;
        } else if (!correctGuess && mCount == 1) {
            mCountdown.setText(String.valueOf(mCount - 1));
            Toast.makeText(
                    this,
                    "NO MORE GUESSES\nGAME OVER",
                    Toast.LENGTH_LONG
            )
                    .show();
            revealCorrectPositions(6);

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

    private void revealCorrectPositions(int correctGuessPositions) {

        switch (correctGuessPositions) {
            case 0:
                mLetterOne.setText(String.valueOf(mCodeWord.charAt(0))
                        .toUpperCase());
                mLetterOne.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 1:
                mLetterTwo.setText(String.valueOf(mCodeWord.charAt(1))
                        .toUpperCase());
                mLetterTwo.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 2:
                mLetterThree.setText(String.valueOf(mCodeWord.charAt(2))
                        .toUpperCase());
                mLetterThree.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 3:
                mLetterFour.setText(String.valueOf(mCodeWord.charAt(3))
                        .toUpperCase());
                mLetterFour.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 4:
                mLetterFive.setText(String.valueOf(mCodeWord.charAt(4))
                        .toUpperCase());
                mLetterFive.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 5:
                mLetterSix.setText(String.valueOf(mCodeWord.charAt(5))
                        .toUpperCase());
                mLetterSix.setVisibility(View.VISIBLE);
                mCorrectCount++;
                break;
            case 6:
                mLetterOne.setText(String.valueOf(mCodeWord.charAt(0))
                        .toUpperCase());
                mLetterOne.setVisibility(View.VISIBLE);
                mLetterTwo.setText(String.valueOf(mCodeWord.charAt(1))
                        .toUpperCase());
                mLetterTwo.setVisibility(View.VISIBLE);
                mLetterThree.setText(String.valueOf(mCodeWord.charAt(2))
                        .toUpperCase());
                mLetterThree.setVisibility(View.VISIBLE);
                mLetterFour.setText(String.valueOf(mCodeWord.charAt(3))
                        .toUpperCase());
                mLetterFour.setVisibility(View.VISIBLE);
                mLetterFive.setText(String.valueOf(mCodeWord.charAt(4))
                        .toUpperCase());
                mLetterFive.setVisibility(View.VISIBLE);
                mLetterSix.setText(String.valueOf(mCodeWord.charAt(5))
                        .toUpperCase());
                mLetterSix.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    public static void hideKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

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
