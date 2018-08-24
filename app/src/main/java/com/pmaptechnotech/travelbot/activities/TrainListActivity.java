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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.adapters.TrainListAdapter;
import com.pmaptechnotech.travelbot.api.railway_api.RApi;
import com.pmaptechnotech.travelbot.api.railway_api.RWebServices;
import com.pmaptechnotech.travelbot.listview.RecyclerTouchListener;
import com.pmaptechnotech.travelbot.listview.Train;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;
import com.pmaptechnotech.travelbot.logics.P;
import com.pmaptechnotech.travelbot.logics.U;
import com.pmaptechnotech.travelbot.models.RTrainsBetweenResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TrainListActivity extends AppCompatActivity implements RecognitionListener {
    @BindView(R.id.txt_date)
    TextView txt_date;
    private List<Train> trainList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TrainListAdapter mAdapter;
    private Context context;
    private TextToSpeech t1;
    private SweetAlertDialog dialog;
    private String srcCode = "", destCode = "", date = "";
    private String src = "", dest = "";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_list);
        context = TrainListActivity.this;
        ButterKnife.bind(this);
        txt_date.setText(U.USER_JOURNEY_DATE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new TrainListAdapter(context, trainList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Train train = trainList.get(position);
                //Toast.makeText(getApplicationContext(), train.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        speechInit();
        t1.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String s) {

                promptSpeechInput("Please Select Your Train Number", P.SELECT_TRAIN);


            }
        });

        src = U.USER_SOURCE;
        dest = U.USER_DEST;
        srcCode = U.USER_SOURCE_CODE;
        destCode = U.USER_DEST_CODE;
        date = U.USER_JOURNEY_DATE;
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
                            (TrainListActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });
        //textToSpeech("What is your Source", P.SOURCE_CITY);
        getTrainsBetween();
        //prepareTrainData();

    }

    private void speechInit() {
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void getTrainsBetween() {
        Retrofit retrofit = RApi.getRetrofitBuilder(this, RApi.getTrainsBetween(srcCode, destCode, date));
        RWebServices webServices = retrofit.create(RWebServices.class);

        dialog = P.showBufferDialog(context, "Processing...");
        //CALL NOW
        webServices.getTrainsBetween()
                .enqueue(new Callback<RTrainsBetweenResult>() {
                    @Override
                    public void onResponse(Call<RTrainsBetweenResult> call, Response<RTrainsBetweenResult> response) {
                        if (dialog.isShowing()) dialog.dismiss();
                        if (!P.analyseResponse(response)) {
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.server_error));
                            return;
                        }
                        RTrainsBetweenResult result = response.body();

                        if (result.response_code == 200) {
                            for (int t = 0; t < result.trains.size(); t++) {
                                JsonElement trArry = result.trains.get(t);
                                JsonObject tr = trArry.getAsJsonObject();
                                String availDays = "";
                                String seatType = "";
                                JsonArray daysArray = tr.getAsJsonArray("days");
                                for (int d = 0; d < daysArray.size(); d++) {
                                    JsonElement dayArryEl = daysArray.get(d);
                                    JsonObject days = dayArryEl.getAsJsonObject();

                                    if (days.get("runs").getAsString().equalsIgnoreCase("Y")) {
                                        availDays = availDays + ", " + days.get("code").getAsString();
                                    }
                                }
                                if (availDays.length() > 2) {
                                    availDays = availDays.substring(1);
                                }
                               /* JsonArray classesArray=tr.getAsJsonArray("classes");
                                for (int d = 0; d < classesArray.size(); d++) {
                                    JsonElement classArryEl=classesArray.get(d);
                                    JsonObject classs=classArryEl.getAsJsonObject();
                                    if (classs.get("available").getAsString().equalsIgnoreCase("Y")) {
                                        seatType = seatType + ", " + classs.get("code").getAsString().substring(0,1);
                                    }
                                }*/

                                trainList.add(new Train(tr.get("number").getAsString(),
                                        tr.get("name").getAsString(),
                                        tr.get("src_departure_time").getAsString(),
                                        tr.get("travel_time").getAsString(),
                                        availDays,
                                        seatType,
                                        src,
                                        dest,
                                        tr.get("dest_arrival_time").getAsString(),
                                        tr.get("travel_time") + "h"));
                            }
                            mAdapter.notifyDataSetChanged();
                            textToSpeech("Select Your Train Number", P.SELECT_TRAIN);
                        } else {
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), "No Train Found");
                        }

                    }

                    @Override
                    public void onFailure(Call<RTrainsBetweenResult> call, Throwable t) {
                        P.displayNetworkErrorMessage(getApplicationContext(), null, t);
                        t.printStackTrace();
                        if (dialog.isShowing()) dialog.dismiss();

                        P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.check_internet_connection));

                    }
                });
    }

    private void prepareTrainData() {
        mAdapter.notifyDataSetChanged();
        askForSelectTrain(10000);
    }

    private void askForSelectTrain(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech("Please Select Your Train Number", P.SELECT_TRAIN);
            }
        }, delay);
    }


    private void textToSpeech(String msg, int requestCode) {

        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, requestCode + "");
        t1.speak(msg, TextToSpeech.QUEUE_FLUSH, myHashRender);
    }

    private void promptSpeechInput(String msg, int requestCode) {
       /* Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, msg);
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }*/

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speech.startListening(recognizerIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case P.SELECT_TRAIN: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(context, "" + result.get(0), Toast.LENGTH_LONG).show();
                    searchTrainNumber(result.get(0));
                }
                break;
            }

        }
    }

    private void searchTrainNumber(String trainNumber) {

        for (int t = 0; t < trainList.size(); t++) {
            Train train = trainList.get(t);
            if (train.getTrainNumber().equalsIgnoreCase(trainNumber) || train.getTrainNumber().startsWith(trainNumber)) {
                Intent intent = new Intent(context, ReservationBookingConfirmationActivity.class);
                ReservationBookingConfirmationActivity.train = train;
                context.startActivity(intent);
                finish();
                return;
            }
        }

        textToSpeech("You have told wrong number train number. Please tell correct train number", P.SELECT_TRAIN);
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
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
        searchTrainNumber(matches.get(0));


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
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
