package com.garzoopvt.garzoo.Login.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResult {

    @SerializedName("user_id")
    @Expose
    public int user_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String isOtp_id() {
        return otp_id;
    }

    public void setOtp_id(String otp_id) {
        this.otp_id = otp_id;
    }

    @SerializedName("name")
    @Expose
    public String name;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("username")
    @Expose
    public String username;

    public LoginResult(int user_id, String name, String username, String otp, String otp_id) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.otp = otp;
        this.otp_id = otp_id;
    }

    @SerializedName("otp")
    @Expose
    public String otp;

    @SerializedName("otp_id")
    @Expose
    public String otp_id;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @SerializedName("latitude")
    @Expose
    public String latitude;


    @SerializedName("longitude")
    @Expose
    public String longitude;

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @SerializedName("taluka")
    @Expose
    public String taluka;

    @SerializedName("area")
    @Expose
    public String area;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @SerializedName("age")
    @Expose
    public String age;
}
