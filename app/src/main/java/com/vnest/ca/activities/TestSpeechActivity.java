package com.vnest.ca.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.vnest.ca.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class TestSpeechActivity extends AppCompatActivity implements RecognitionListener {

    private static final String trigger = "open";
    private static final String KWS_SEARCH = "ok alex";
    private static final String KEYPHRASE = "ok alex";
    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    final MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_speech);
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(TestSpeechActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);

                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    result.printStackTrace();
                } else {
                    recognizer.startListening(trigger);
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) {

        try {

            // Create keyword-activation search


//            // Create grammar-based searches
//            File menuGrammar = new File(assetsDir, "menu.gram");
//            recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
//
//            // Next search for digits
//            File digitsGrammar = new File(assetsDir, "digits.gram");
//            recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
//
//            // Create language model search
//            File languageModel = new File(assetsDir, "weather.dmp");
//            recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
            recognizer = defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                    .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setKeywordThreshold(1e-45f)
                    .getRecognizer();
            recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
            recognizer.addListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e("Begin speech", "Ok");
    }

    @Override
    public void onEndOfSpeech() {
        Log.e("End speech", "Ok");

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        Log.e("onPartialResult", "Ok");

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        Log.e("onResult", "Ok");

    }

    @Override
    public void onError(Exception e) {
        Log.e("onError", e.getMessage(), e);

    }

    @Override
    public void onTimeout() {
        Log.e("onTimeout", "Ok");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }
}
