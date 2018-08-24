package com.pmaptechnotech.travelbot.listview;

/**
 * Created by Lincoln on 15/01/16.
 */
public class Train {
    private String trainNumber,
            trainName,
            trainDeparture,
            trainDuration,
            trainDays,
            seatsType,
            departureCity,
            arrivalCity,
            arrivalTime,
            trainType;

    public Train() {
    }

    public Train(String trainNumber, String trainName, String trainDeparture, String trainDuration, String trainDays, String seatsType, String departureCity, String arrivalCity, String arrivalTime, String trainType) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.trainDeparture = trainDeparture;
        this.trainDuration = trainDuration;
        this.seatsType = seatsType;
        this.trainDays = trainDays;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.arrivalTime = arrivalTime;
        this.trainType = trainType;
        /*   this.train_journy_price = train_journy_price;*/

    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }


    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }


    public String getSeatsType() {
        return seatsType;
    }

    public void setSeatsType(String seatsType) {
        this.seatsType = seatsType;
    }


    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }


    public String getTrainDeparture() {
        return trainDeparture;
    }

    public void setTrainDeparture(String trainDeparture) {
        this.trainDeparture = trainDeparture;
    }


    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }


    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


    public String getTrainDays() {
        return trainDays;
    }

    public void setTrainDays(String trainDays) {
        this.trainDays = trainDays;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }


    public String getTrainDuration() {
        return trainDuration;
    }

    public void setTrainDuration(String trainDuration) {
        this.trainDuration = trainDuration;
    }


  /*  public String getTrain_journy_price() {
        return train_journy_price;
    }

    public void setTrain_journy_price(String train_journy_price) {this.train_journy_price = train_journy_price;}
*/


}
