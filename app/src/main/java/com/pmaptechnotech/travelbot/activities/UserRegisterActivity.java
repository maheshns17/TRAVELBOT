package com.pmaptechnotech.travelbot.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.api.Api;
import com.pmaptechnotech.travelbot.api.WebServices;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;
import com.pmaptechnotech.travelbot.logics.P;
import com.pmaptechnotech.travelbot.logics.U;
import com.pmaptechnotech.travelbot.models.UserRegisterInput;
import com.pmaptechnotech.travelbot.models.UserRegisterResult;

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

public class UserRegisterActivity extends AppCompatActivity implements RecognitionListener {
    @BindView(R.id.edt_Name)
    EditText edt_Name;
    @BindView(R.id.edt_Email_id)
    EditText edt_Email_id;
    @BindView(R.id.edt_Mobile)
    EditText edt_Mobile;
    @BindView(R.id.edt_Password)
    EditText edt_Password;
    @BindView(R.id.btn_skip)
    Button btn_skip;
    @BindView(R.id.btn_register)
    Button btn_register;
    TextToSpeech t1;
    private Context context;
    private SweetAlertDialog dialog;

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
        setContentView(R.layout.activity_user_register);
        context = UserRegisterActivity.this;
        ButterKnife.bind(this);


        btn_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(context, UserLoginActivity.class);
                startActivity(intent);
            }
        });
        // promptSpeechInput("What is your source?", P.SOURCE_CITY);


        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SourceAndDestinationActivity.class);
                startActivity(intent);
            }
        });


        edt_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput("What is your name?", P.ENTER_NAME);
            }
        });

        edt_Email_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput("What is your email id?", P.ENTER_EMAIL_ID);
            }
        });

        edt_Mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput("What is your mobile number?", P.ENTER_MOBILE);
            }
        });

        edt_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput("What is your password?", P.ENTER_PASSWORD);
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
                            (UserRegisterActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });
        textToSpeech("Do you want to skip registration?", P.SKIP);


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
                    case P.SKIP + "":
                        promptSpeechInput("Skip?", P.SKIP);
                        break;
                    case P.ENTER_NAME + "":
                        promptSpeechInput("What is your name?", P.ENTER_NAME);
                        break;
                    case P.ENTER_EMAIL_ID + "":
                        promptSpeechInput("What is your email id?", P.ENTER_EMAIL_ID);
                        break;
                    case P.ENTER_MOBILE + "":
                        promptSpeechInput("What is your mobile number?", P.ENTER_MOBILE);
                        break;
                    case P.ENTER_PASSWORD + "":
                        promptSpeechInput("What is your password?", P.ENTER_PASSWORD);
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
            case P.SKIP:{
                if(matches.get(0).toLowerCase().contains("no")){
                    textToSpeech("What is your Name", P.ENTER_NAME);
                }else{
                    Intent intent = new Intent(context, StateAndLanguageActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case P.ENTER_NAME: {
                edt_Name.setText(matches.get(0).replace(" ",""));
                textToSpeech("Your Mobile Number", P.ENTER_MOBILE);
                break;
            }
            case P.ENTER_MOBILE: {

                edt_Mobile.setText(matches.get(0).replace(" ",""));
                textToSpeech("Password Please", P.ENTER_PASSWORD);

                break;
            }

            case P.ENTER_PASSWORD: {
                edt_Password.setText(matches.get(0).replace(" ",""));
                U.USER_NAME = edt_Name.getText().toString().trim();
                U.USER_EMAIL = edt_Email_id.getText().toString().trim();
                U.USER_MOBILE = edt_Mobile.getText().toString().trim();
                U.ENTER_PASSWORD = edt_Password.getText().toString().trim();

                userRegister();
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


    private void userRegister() {

        Retrofit retrofit = Api.getRetrofitBuilder(this);
        WebServices webServices = retrofit.create(WebServices.class);

        //PREPARE INPUT/REQUEST PARAMETERS
        UserRegisterInput userRegisterInput = new UserRegisterInput(
                edt_Name.getText().toString().trim(),
                edt_Email_id.getText().toString().trim(),
                edt_Mobile.getText().toString().trim(),
                edt_Password.getText().toString().trim()

        );
        dialog = P.showBufferDialog(context, "Processing...");
        // btn_Submit.setProgress(1);
        btn_register.setEnabled(false);
        P.hideSoftKeyboard(UserRegisterActivity.this);
        //CALL NOW
        webServices.userRegister(userRegisterInput)
                .enqueue(new Callback<UserRegisterResult>() {
                    @Override
                    public void onResponse(Call<UserRegisterResult> call, Response<UserRegisterResult> response) {
                        if (dialog.isShowing()) dialog.dismiss();
                        if (!P.analyseResponse(response)) {
                            // btn_Submit.setProgress(0);
                            btn_register.setEnabled(true);
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.server_error));
                            return;
                        }
                        UserRegisterResult result = response.body();

                        if (result.is_success) {
                            // btn_Submit.setProgress(100);
                            Toast.makeText(context, " You Have Successfully registered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, UserLoginActivity.class);
                            startActivity(intent);
                            finish();
                        }


                        else {
                            //  btn_Submit.setProgress(0);
                            btn_register.setEnabled(true);
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), result.msg);
                        }

                    }

                    @Override
                    public void onFailure(Call<UserRegisterResult> call, Throwable t) {
                        P.displayNetworkErrorMessage(getApplicationContext(), null, t);
                        t.printStackTrace();
                        if (dialog.isShowing()) dialog.dismiss();
                        //  btn_Submit.setProgress(0);
                        btn_register.setEnabled(true);
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




