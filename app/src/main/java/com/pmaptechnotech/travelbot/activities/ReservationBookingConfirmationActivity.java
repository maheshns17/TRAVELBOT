package com.pmaptechnotech.travelbot.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.api.Api;
import com.pmaptechnotech.travelbot.api.WebServices;
import com.pmaptechnotech.travelbot.listview.Train;
import com.pmaptechnotech.travelbot.logics.LocaleHelper;
import com.pmaptechnotech.travelbot.logics.P;
import com.pmaptechnotech.travelbot.logics.U;
import com.pmaptechnotech.travelbot.models.UserBookingInput;
import com.pmaptechnotech.travelbot.models.UserBookingResult;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReservationBookingConfirmationActivity extends AppCompatActivity {
    public static Train train;
    @BindView(R.id.pb_payment)
    ProgressBar pb_payment;
    @BindView(R.id.txt_train_name)
    TextView txt_train_name;
    @BindView(R.id.txt_train_number)
    TextView txt_train_number;
    @BindView(R.id.txt_Cost)
    TextView txt_Cost;
    @BindView(R.id.txt_Source)
    TextView txt_Source;
    @BindView(R.id.txt_Destination)
    TextView txt_Destination;
    private Context context;
    private SweetAlertDialog dialog;

    private TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_booking_confirmation);
        ButterKnife.bind(this);
        context = ReservationBookingConfirmationActivity.this;
        Train t = ReservationBookingConfirmationActivity.train;
        txt_train_name.setText(t.getTrainName());
        txt_train_number.setText(t.getTrainNumber());
        txt_Cost.setText("Rs. " + new Random(500).nextInt() + "");
        txt_Source.setText(t.getArrivalCity());
        txt_Destination.setText(t.getDepartureCity());
        speechInit();
        t1.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String s) {


            }
        });
        userBooking();
        askForSelectTrain(4000);
        //paymentDelay();
    }

    private void textToSpeech(String msg, int requestCode) {

        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, requestCode + "");
        t1.speak(msg, TextToSpeech.QUEUE_FLUSH, myHashRender);
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

    private void askForSelectTrain(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech("Please Wait, We are processing your Your Payment", P.SELECT_TRAIN);
            }
        }, delay);
    }

    private void paymentDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, BookedStatusAndThankyouActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }


    private void userBooking() {

        Retrofit retrofit = Api.getRetrofitBuilder(this);
        WebServices webServices = retrofit.create(WebServices.class);
        Train t = ReservationBookingConfirmationActivity.train;

        String user_name = "";
        String user_mobile_number = "";
        if(U.user!=null) {
            if (U.user.user_name != null) {
                user_name = U.user.user_name;
            }

            if (U.user.user_mobile_number != null) {
                user_mobile_number = U.user.user_mobile_number;
            }
        }


        //PREPARE INPUT/REQUEST PARAMETERS
        UserBookingInput userBookingInput = new UserBookingInput(
                user_name,
                user_mobile_number,
                txt_train_name.getText().toString().trim(),
                txt_train_number.getText().toString().trim(),
                txt_Cost.getText().toString().trim(),
                txt_Source.getText().toString().trim(),
                txt_Destination.getText().toString().trim(),
                U.USER_JOURNEY_DATE,
                t.getTrainDuration()


        );
        dialog = P.showBufferDialog(context, "Processing...");
        // btn_Submit.setProgress(1);
        // btn_register.setEnabled(false);
        P.hideSoftKeyboard(ReservationBookingConfirmationActivity.this);
        //CALL NOW
        webServices.userBooking(userBookingInput)
                .enqueue(new Callback<UserBookingResult>() {
                    @Override
                    public void onResponse(Call<UserBookingResult> call, Response<UserBookingResult> response) {
                        if (dialog.isShowing()) dialog.dismiss();
                        if (!P.analyseResponse(response)) {
                            // btn_Submit.setProgress(0);
                            // btn_register.setEnabled(true);
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.server_error));
                            return;
                        }
                        UserBookingResult result = response.body();

                        if (result.is_success) {
                            paymentDelay();
                        } else {
                            //  btn_Submit.setProgress(0);
                            // btn_register.setEnabled(true);
                            P.ShowErrorDialogAndBeHere(context, getString(R.string.error), result.msg);
                        }

                    }

                    @Override
                    public void onFailure(Call<UserBookingResult> call, Throwable t) {
                        P.displayNetworkErrorMessage(getApplicationContext(), null, t);
                        t.printStackTrace();
                        if (dialog.isShowing()) dialog.dismiss();
                        //  btn_Submit.setProgress(0);
                        // btn_register.setEnabled(true);
                        P.ShowErrorDialogAndBeHere(context, getString(R.string.error), getString(R.string.check_internet_connection));

                    }
                });
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
