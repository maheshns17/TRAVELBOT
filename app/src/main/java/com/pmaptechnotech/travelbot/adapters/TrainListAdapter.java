package com.pmaptechnotech.travelbot.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pmaptechnotech.travelbot.R;
import com.pmaptechnotech.travelbot.activities.ReservationBookingConfirmationActivity;
import com.pmaptechnotech.travelbot.listview.Train;

import java.util.List;

public class TrainListAdapter extends RecyclerView.Adapter<TrainListAdapter.MyViewHolder> {

    private List<Train> trainList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView train_duration, train_departure, train_name, train_number,seats_type, departure_city,arrival_city,train_days,arrival_time,train_type;
        public LinearLayout lnr_train_details;

        public MyViewHolder(View view) {
            super(view);
            train_duration = (TextView) view.findViewById(R.id.train_duration);
            train_departure = (TextView) view.findViewById(R.id.train_departure);
            train_name = (TextView) view.findViewById(R.id.train_name);
            train_number = (TextView) view.findViewById(R.id.train_number);
            train_days = (TextView) view.findViewById(R.id.train_days);
            seats_type = (TextView) view.findViewById(R.id.seats_type);
            departure_city = (TextView) view.findViewById(R.id.departure_city);
            arrival_city = (TextView) view.findViewById(R.id.arrival_city);
            arrival_time = (TextView) view.findViewById(R.id.arrival_time);
            train_type = (TextView) view.findViewById(R.id.train_type);

            lnr_train_details = (LinearLayout) view.findViewById(R.id.lnr_train_details);
        }
    }


    public TrainListAdapter(Context context, List<Train> trainList) {
        this.trainList = trainList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.train_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Train train = trainList.get(position);
        holder.train_duration.setText(train.getTrainDuration());
        holder.train_departure.setText(train.getTrainDeparture());
        holder.train_number.setText(train.getTrainNumber());
        holder.train_name.setText(train.getTrainName());
        holder.train_days.setText(train.getTrainDays());
        holder.seats_type.setText(train.getSeatsType());
        holder.departure_city.setText(train.getDepartureCity());
        holder.arrival_city.setText(train.getArrivalCity());
        holder.arrival_time.setText(train.getArrivalTime());
        holder.train_type.setText(train.getTrainType());

      /* holder.train_journey_price.setText("Rs " + train.getTrain_journy_price());*/

        holder.lnr_train_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReservationBookingConfirmationActivity.class);
                ReservationBookingConfirmationActivity.train = train;
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainList.size();
    }


}
