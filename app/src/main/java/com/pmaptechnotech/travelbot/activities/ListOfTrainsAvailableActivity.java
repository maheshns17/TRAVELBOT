package com.pmaptechnotech.travelbot.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.logics.U;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListOfTrainsAvailableActivity extends AppCompatActivity {
    @BindView(R.id.txt_date)
    TextView txt_date;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_trains_available);
        ButterKnife.bind(this);
        context = ListOfTrainsAvailableActivity.this;
        txt_date.setText(U.USER_JOURNEY_DATE);

    }

}
