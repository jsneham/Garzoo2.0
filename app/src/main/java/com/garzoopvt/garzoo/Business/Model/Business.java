package com.garzoopvt.garzoo.Business.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "business")
public class Business implements Parcelable {
    
    
    @PrimaryKey
    @NonNull
    private  String id;
    
     @ColumnInfo(name ="distance")
     private  String distance;


     @ColumnInfo(name ="image_id")
     private  String image_id;
    private  String video;


     @ColumnInfo(name ="interest_status")
    private  String interest_status;

     @ColumnInfo(name ="media_url")
     private  String media_url;


     @ColumnInfo(name ="mobile_status")
    private  String mobile_status;




     @ColumnInfo(name ="mobile")
    private  String mobile;

     @ColumnInfo(name ="admin_id")
    private  String admin_id;

     @ColumnInfo(name ="category_id")
    private  String category_id;

     @ColumnInfo(name ="user_id")
    private  String user_id;

     @ColumnInfo(name ="title")
    private  String title;

     @ColumnInfo(name ="description")
    private  String description;

     @ColumnInfo(name ="address")
    private  String address;

     @ColumnInfo(name ="price")
    private  String price;

     @ColumnInfo(name ="image")
    private  String image;

     @ColumnInfo(name ="images")
    private  String images;

     @ColumnInfo(name ="latitude")
    private  String latitude;

     @ColumnInfo(name ="longitude")
    private  String longitude;

     @ColumnInfo(name ="status")
    private  String status;

     @ColumnInfo(name ="last_modified")
    private  String last_modified;

     @ColumnInfo(name ="dt")
    private  String dt;

     @ColumnInfo(name ="fname")
    private  String fname;

     @ColumnInfo(name ="lname")
    private  String lname;

     @ColumnInfo(name ="category")
    private  String category;

     @ColumnInfo(name ="area")
    private  String area;

     @ColumnInfo(name ="taluka")
    private  String taluka;

    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;


    public Business() {
    }

    public Business(@NonNull String id, String distance, String image_id, String video, String interest_status, String media_url, String mobile_status, String mobile, String admin_id, String category_id, String user_id, String title, String description, String address, String price, String image, String images, String latitude, String longitude, String status, String last_modified, String dt, String fname, String lname, String category, String area, String taluka, int timestamp) {
        this.id = id;
        this.distance = distance;
        this.image_id = image_id;
        this.video = video;
        this.interest_status = interest_status;
        this.media_url = media_url;
        this.mobile_status = mobile_status;
        this.mobile = mobile;
        this.admin_id = admin_id;
        this.category_id = category_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
        this.image = image;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.last_modified = last_modified;
        this.dt = dt;
        this.fname = fname;
        this.lname = lname;
        this.category = category;
        this.area = area;
        this.taluka = taluka;
        this.timestamp = timestamp;
    }

    protected Business(Parcel in) {
        id = in.readString();
        distance = in.readString();
        image_id = in.readString();
        video = in.readString();
        interest_status = in.readString();
        media_url = in.readString();
        mobile_status = in.readString();
        mobile = in.readString();
        admin_id = in.readString();
        category_id = in.readString();
        user_id = in.readString();
        title = in.readString();
        description = in.readString();
        address = in.readString();
        price = in.readString();
        image = in.readString();
        images = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        status = in.readString();
        last_modified = in.readString();
        dt = in.readString();
        fname = in.readString();
        lname = in.readString();
        category = in.readString();
        area = in.readString();
        taluka = in.readString();
        timestamp = in.readInt();
    }

    public static final Creator<Business> CREATOR = new Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getInterest_status() {
        return interest_status;
    }

    public void setInterest_status(String interest_status) {
        this.interest_status = interest_status;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getMobile_status() {
        return mobile_status;
    }

    public void setMobile_status(String mobile_status) {
        this.mobile_status = mobile_status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(distance);
        parcel.writeString(image_id);
        parcel.writeString(video);
        parcel.writeString(interest_status);
        parcel.writeString(media_url);
        parcel.writeString(mobile_status);
        parcel.writeString(mobile);
        parcel.writeString(admin_id);
        parcel.writeString(category_id);
        parcel.writeString(user_id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(address);
        parcel.writeString(price);
        parcel.writeString(image);
        parcel.writeString(images);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(status);
        parcel.writeString(last_modified);
        parcel.writeString(dt);
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(category);
        parcel.writeString(area);
        parcel.writeString(taluka);
        parcel.writeInt(timestamp);
    }
}
