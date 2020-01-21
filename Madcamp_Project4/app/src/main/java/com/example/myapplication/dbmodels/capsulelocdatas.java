package com.example.myapplication.dbmodels;


public class capsulelocdatas {
    private final String capsuleid;
    private final Double latitude;
    private final Double longtitude;
    private final Double altitude;

    public  capsulelocdatas(String capsuleId, Double Lat, Double Long, Double Al){
            this.capsuleid = capsuleId;
            this.latitude = Lat;
            this.longtitude = Long;
            this.altitude = Al;
    }
    public String getCapsuleId(){return capsuleid; }
    public  Double getLatitude(){
        return latitude;
    }
    public  Double getLongtitude(){
        return longtitude;
    }
    public  Double getAltitude(){
        return altitude;
    }

}
