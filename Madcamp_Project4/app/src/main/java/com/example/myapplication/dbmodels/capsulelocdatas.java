package com.example.myapplication.dbmodels;


public class capsulelocdatas {
    private final String CapsuleId;
    private final Double Latitude;
    private final Double Longtitude;
    private final Double Altitude;

    public  capsulelocdatas(String capsuleId, Double Lat, Double Long, Double Al){
            this.CapsuleId = capsuleId;
            this.Latitude = Lat;
            this.Longtitude = Long;
            this.Altitude = Al;
    }
    public String getCapsuleId(){
        return CapsuleId;
    }
    public  Double getLatitude(){
        return Latitude;
    }
    public  Double getLongtitude(){
        return Longtitude;
    }
    public  Double getAltitude(){
        return Altitude;
    }

}
