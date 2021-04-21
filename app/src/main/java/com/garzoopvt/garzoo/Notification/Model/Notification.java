package com.garzoopvt.garzoo.Notification.Model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "notification")
public class Notification implements Parcelable {

    @PrimaryKey
    @NonNull
    private String notification_id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "from_name")
    private  String from_name;

    @ColumnInfo(name = "phone")
    private  String phone;

    @ColumnInfo(name = "from_id")
    private String from_id;

    @ColumnInfo(name = "image")
    private  String image;

    @ColumnInfo(name = "type")
    private  String type;

    @ColumnInfo(name = "date")
    private String date;


    public Notification(@NonNull String notification_id, String title, String from_name, String phone, String from_id, String image, String type, String date) {
        this.notification_id = notification_id;
        this.title = title;
        this.from_name = from_name;
        this.phone = phone;
        this.from_id = from_id;
        this.image = image;
        this.type = type;
        this.date = date;
    }

    public Notification(){

    }


    protected Notification(Parcel in) {
        notification_id = in.readString();
        title = in.readString();
        from_name = in.readString();
        phone = in.readString();
        from_id = in.readString();
        image = in.readString();
        type = in.readString();
        date = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(notification_id);
        parcel.writeString(title);
        parcel.writeString(from_name);
        parcel.writeString(phone);
        parcel.writeString(from_id);
        parcel.writeString(image);
        parcel.writeString(type);
        parcel.writeString(date);
    }


    @NonNull
    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(@NonNull String notification_id) {
        this.notification_id = notification_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Notification{" +
                "notification_id='" + notification_id + '\'' +
                ", title='" + title + '\'' +
                ", from_name='" + from_name + '\'' +
                ", phone='" + phone + '\'' +
                ", from_id='" + from_id + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
