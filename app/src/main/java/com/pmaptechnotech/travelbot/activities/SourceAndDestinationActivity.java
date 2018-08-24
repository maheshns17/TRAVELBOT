package com.pmaptechnotech.travelbot.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.api.railway_api.RApi;
import com.pmaptechnotech.travelbot.api.railway_api.RWebServices;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;
import com.pmaptechnotech.travelbot.logics.P;
import com.pmaptechnotech.travelbot.logics.U;
import com.pmaptechnotech.travelbot.models.RNameToCodeResult;
import com.pmaptechnotech.travelbot.models.RNameToCodeStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SourceAndDestinationActivity extends AppCompatActivity implements RecognitionListener {
    @BindView(R.id.edt_from_city)
    EditText edt_from_city;
    @BindView(R.id.edt_to_city)
    EditText edt_to_city;
    @BindView(R.id.edt_journey_date)
    EditText edt_journey_date;
    TextToSpeech t1;
    private Context context;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_and_destination);
        ButterKnife.bind(this);
        context = SourceAndDestinationActivity.this;
        // promptSpeechInput("What is your source?", P.SOURCE_CITY);

        edt_from_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = P.SOURCE_CITY;
                promptSpeechInput("What is your source?", P.SOURCE_CITY);
            }
        });

        edt_to_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = P.DESTINATION_CITY;
                promptSpeechInput("What is your destination?", P.DESTINATION_CITY);
            }
        });

        edt_journey_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = P.JOURNEY_DATE;
                promptSpeechInput("Journey Date?", P.JOURNEY_DATE);
            }
        });


        //SPEECH RECOGNIZER
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);


                    ActivityCompat.requestPermissions
                            (SourceAndDestinationActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });
        textToSpeech("What is your Source", P.SOURCE_CITY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(SourceAndDestinationActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
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
            case P.SOURCE_CITY: {

                edt_from_city.setText(matches.get(0));
                getNameToCode(edt_from_city.getText().toString().trim(), true);

                break;
            }
            case P.DESTINATION_CITY: {

                edt_to_city.setText(matches.get(0));
                getNameToCode(edt_to_city.getText().toString().trim(), false);

                break;
            }

            case P.JOURNEY_DATE: {
                edt_journey_date.setText(matches.get(0));
                String jDate[] = matches.get(0).split(" ");
                if (jDate.length < 3) {
                    speech.startListening(recognizerIntent);
                }
                edt_journey_date.setText(jDate[0] + "-" + U.getMonthFromName(jDate[1]) + "-" + jDate[2]);

                U.USER_SOURCE = edt_from_city.getText().toString().trim();
                U.USER_DEST = edt_to_city.getText().toString().trim();
                U.USER_JOURNEY_DATE = edt_journey_date.getText().toString().trim();
                Intent intent = new Intent(context, TrainListActivity.class);
                startActivity(intent);
                finish();
                break;
            }

        }

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
                    case P.SOURCE_CITY + "":
                        promptSpeechInput("What is your source?", P.SOURCE_CITY);
                        break;
                    case P.DESTINATION_CITY + "":
                        promptSpeechInput("What is your destination?", P.DESTINATION_CITY);
                        break;
                    case P.JOURNEY_DATE + "":
                        promptSpeechInput("Journey Date", P.JOURNEY_DATE);
                        break;
                }

            }
        });
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


    private void getNameToCode(String name, final boolean isSource) {
        Retrofit retrofit = RApi.getRetrofitBuilder(this, RApi.getNameToCodeUrl(name));
        RWebServices webServices = retrofit.create(RWebServices.class);

        final SweetAlertDialog dialog = P.showBufferDialog(context, "Processing...");
        //CALL NOW
        webServices.getCodeFromName()
                .enqueue(new Callback<RNameToCodeResult>() {
                    @Override
                    public void onResponse(Call<RNameToCodeResult> call, Response<RNameToCodeResult> response) {
                        if (dialog.isShowing()) dialog.dismiss();
                        if (!P.analyseResponse(response)) {
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.server_error));
                            return;
                        }
                        RNameToCodeResult result = response.body();

                        if (result.response_code == 200) {
                            if (result.stations != null && result.stations.size() > 0) {
                                RNameToCodeStation station = result.stations.get(0);
                                if (isSource) {
                                    U.USER_SOURCE_CODE = station.code;
                                    textToSpeech("What is your Destination", P.DESTINATION_CITY);
                                } else {
                                    U.USER_DEST_CODE = station.code;
                                    textToSpeech("Journey Date", P.JOURNEY_DATE);
                                }
                            } else {

                                if (isSource) {
                                    textToSpeech("Sorry We could not find CODE. Please repeat again", P.SOURCE_CITY);
                                } else {
                                    textToSpeech("Sorry We could not find CODE. Please repeat again", P.DESTINATION_CITY);
                                }


                            }

                        } else {
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), "No Code Found");
                        }

                    }

                    @Override
                    public void onFailure(Call<RNameToCodeResult> call, Throwable t) {
                        P.displayNetworkErrorMessage(getApplicationContext(), null, t);
                        t.printStackTrace();
                        if (dialog.isShowing()) dialog.dismiss();

                        P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.check_internet_connection));

                    }
                });
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
