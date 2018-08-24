package com.pmaptechnotech.travelbot.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;
import com.pmaptechnotech.travelbot.logics.P;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StateAndLanguageActivity extends AppCompatActivity implements RecognitionListener {
    @BindView(R.id.select_state)
    SearchableSpinner select_state;
    @BindView(R.id.select_language)
    SearchableSpinner select_language;
    @BindView(R.id.txt_language)
    TextView txt_language;
    @BindView(R.id.txt_state)
    TextView txt_state;
    @BindView(R.id.btn_continue)
    Button btn_continue;

    private Context context;

    TextToSpeech t1;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "SPEECHRECH";
    private final int REQUEST_RECORD_PERMISSION = 100;
    @BindView(R.id.imageView1)
    ImageView imageView;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar;
    @BindView(R.id.textView1)
    TextView textView;
    @BindView(R.id.toggleButton1)
    ToggleButton toggleButton;
    private int currentCode = 0;
    private List<String> stateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_and_language);
        ButterKnife.bind(this);
        context = StateAndLanguageActivity.this;


        btn_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(context, SourceAndDestinationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        setSpinner();
        select_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        updateViews("en");
                        break;
                    case 2:
                        updateViews("hi");
                        break;
                    case 3:
                        updateViews("kn");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);


                    ActivityCompat.requestPermissions
                            (StateAndLanguageActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });
        stateList.add("Andhra Pradesh");
        stateList.add("Arunachal Pradesh");
        stateList.add("Assam");
        stateList.add("Bihar");
        stateList.add("Goa");
        stateList.add("Chhattisgarh");
        stateList.add("Gujarat");
        stateList.add("Haryana");
        stateList.add("Himachal Pradesh");
        stateList.add("Jammu and Kashmir");
        stateList.add("Jharkhand");
        stateList.add("Karnataka");
        stateList.add("Kerala");
        stateList.add("Madhya Pradesh");
        stateList.add("Maharashtra");
        stateList.add("Manipur");
        stateList.add("Meghalaya");
        stateList.add("Mizoram");
        stateList.add("Nagaland");
        stateList.add("Odisha");
        stateList.add("Punjab");
        stateList.add("Rajasthan");
        stateList.add("Sikkim");
        stateList.add("Tamil Nadu");
        stateList.add("Telangana");
        stateList.add("Tripura");
        stateList.add("Uttar Pradesh");
        stateList.add("Uttarakhand");
        stateList.add("West Bengal");
        stateList.add("Andaman and Nicobar Islands");
        stateList.add("Chandigarh");
        stateList.add("Dadar and Nagar Haveli");
        stateList.add("Daman and Diu");
        stateList.add("Delhi");
        stateList.add("Lakshadweep");
        stateList.add("Puducherry");

        textToSpeech("Tell me your State in India", P.ENTER_STATE);

    }


    private void speechInit() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d("SpeechInit", "Status : " + status);
                if (status == TextToSpeech.SUCCESS) {
                    t1.setLanguage(Locale.UK);
                    Log.d("SpeechInit", "Status : Succ");
                } else if (status == TextToSpeech.ERROR) {
                    Log.d("SpeechInit", "Status : Error");
                }
            }
        });
        t1.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String s) {
                Log.d("Source", "S: " + s);
                switch (s) {
                    case P.ENTER_STATE + "":
                        promptSpeechInput("State", P.ENTER_STATE);
                        break;
                    case P.ENTER_LANGUAGE + "":
                        promptSpeechInput("Language?", P.ENTER_LANGUAGE);
                        break;
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        textView.setText(errorMessage);
        toggleButton.setChecked(false);
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        textView.setText(text);

        switch (currentCode) {

            case P.ENTER_STATE: {

                txt_state.setText(matches.get(0));
                boolean isFound = false;
                for (int s = 0; s < stateList.size(); s++) {
                    if (stateList.get(s).toLowerCase().contains(matches.get(0).toLowerCase())) {
                        isFound = true;
                        break;
                    }
                }

                if (isFound) {
                    textToSpeech("Please tell me your Language", P.ENTER_LANGUAGE);
                } else {
                    textToSpeech("We could not find your State, Please tell me your State in India", P.ENTER_STATE);
                }


                break;
            }

            case P.ENTER_LANGUAGE: {
                txt_language.setText(matches.get(0));
                checkLanguage(matches.get(0));

                break;
            }

        }

    }

    private void checkLanguage(String str) {
        if (str.toLowerCase().contains("kannada")) {//ಕನ್ನಡ
            updateViews("kn");
            nextActivity();
        } else if (str.toLowerCase().contains("english")) {
            updateViews("en");
            nextActivity();
        } else if (str.toLowerCase().contains("hindi")) {
            updateViews("hi");
            nextActivity();
        } else {
            textToSpeech("We could not recognize your language, Please tell me your Language again", P.ENTER_LANGUAGE);
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(context, SourceAndDestinationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }


    private void textToSpeech(final String msg, final int requestCode) {
        currentCode = requestCode;
        speechInit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> myHashRender = new HashMap<String, String>();
                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, requestCode + "");
                t1.speak(msg, TextToSpeech.QUEUE_FLUSH, myHashRender);
            }
        }, 2000);
    }

    private void promptSpeechInput(String msg, int requestCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speech.startListening(recognizerIntent);
            }
        });

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();

       /* mTitleTextView.setText(resources.getString(R.string.main_activity_title));
        mDescTextView.setText(resources.getString(R.string.main_activity_desc));
        mAboutTextView.setText(resources.getString(R.string.main_activity_about));
        mToTRButton.setText(resources.getString(R.string.main_activity_to_tr_button));
        mToENButton.setText(resources.getString(R.string.main_activity_to_en_button));

        setTitle(resources.getString(R.string.main_activity_toolbar_title));*/
    }

    private void validation() {

       /* if (select_state.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select state", Toast.LENGTH_LONG).show();
            return;
        }

        if (select_language.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select language", Toast.LENGTH_LONG).show();
            return;
        }*/

        Intent intent = new Intent(context, SourceAndDestinationActivity.class);
        startActivity(intent);

       /* stateAndLanguage();*/
    }


    private void setSpinner() {
        P.setSpinnerAdapter(context, select_state, getResources().getStringArray(R.array.select_state));
        P.setSpinnerAdapter(context, select_language, getResources().getStringArray(R.array.select_language));

    }

}
