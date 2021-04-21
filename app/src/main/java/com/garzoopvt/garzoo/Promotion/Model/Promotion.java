package com.garzoopvt.garzoo.Promotion.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "promotion")
public class Promotion implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;
    
    @ColumnInfo(name ="distance")
    private String distance;
    

    @ColumnInfo(name ="image_id")
    private String image_id;

    @ColumnInfo(name ="video")
    private String video;
    
    @ColumnInfo(name ="interest_status")
    private String interest_status;

  
    @ColumnInfo(name ="address")
    private String address;

    @ColumnInfo(name ="media_url")
    private String media_url;
    
    @ColumnInfo(name ="mobile_status")
    private String mobile_status;
    

    @ColumnInfo(name ="admin_id")
    private String admin_id;

    @ColumnInfo(name ="user_id")
    private String user_id;

    @ColumnInfo(name ="title")
    private String title;

    @ColumnInfo(name ="description")
    private String description;

    @ColumnInfo(name ="image")
    private String image;

    @ColumnInfo(name ="images")
    private String images;

    @ColumnInfo(name ="latitude")
    private String latitude;

    @ColumnInfo(name ="longitude")
    private String longitude;

    @ColumnInfo(name ="pd_status")
    private String pd_status;

    @ColumnInfo(name ="status")
    private String status;

    @ColumnInfo(name ="last_modified")
    private String last_modified;

    @ColumnInfo(name ="dt")
    private String dt;

    @ColumnInfo(name ="fname")
    private String fname;

    @ColumnInfo(name ="lname")
    private String lname;

    @ColumnInfo(name ="mobile")
    private String mobile;

    @ColumnInfo(name ="area")
    private  String area;

    @ColumnInfo(name ="taluka")
    private  String taluka;

    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;


    @ColumnInfo(name = "block_status")
    private String block_status;

    public Promotion() {
    }

    public Promotion(@NonNull String id, String distance, String image_id, String video, String interest_status, String address, String media_url, String mobile_status, String admin_id, String user_id, String title, String description, String image, String images, String latitude, String longitude, String pd_status, String status, String last_modified, String dt, String fname, String lname, String mobile, String area, String taluka, int timestamp,
                     String block_status) {
        this.id = id;
        this.distance = distance;
        this.image_id = image_id;
        this.video = video;
        this.interest_status = interest_status;
        this.address = address;
        this.media_url = media_url;
        this.mobile_status = mobile_status;
        this.admin_id = admin_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pd_status = pd_status;
        this.status = status;
        this.last_modified = last_modified;
        this.dt = dt;
        this.fname = fname;
        this.lname = lname;
        this.mobile = mobile;
        this.area = area;
        this.taluka = taluka;
        this.timestamp = timestamp;
        this.block_status = block_status;
    }

    protected Promotion(Parcel in) {
        id = in.readString();
        distance = in.readString();
        image_id = in.readString();
        video = in.readString();
        interest_status = in.readString();
        address = in.readString();
        media_url = in.readString();
        mobile_status = in.readString();
        admin_id = in.readString();
        user_id = in.readString();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        images = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        pd_status = in.readString();
        status = in.readString();
        last_modified = in.readString();
        dt = in.readString();
        fname = in.readString();
        lname = in.readString();
        mobile = in.readString();
        area = in.readString();
        taluka = in.readString();
        timestamp = in.readInt();
        block_status = in.readString();
    }

    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
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

    public String getPd_status() {
        return pd_status;
    }

    public void setPd_status(String pd_status) {
        this.pd_status = pd_status;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getBlock_status() {
        return block_status;
    }

    public void setBlock_status(String block_status) {
        this.block_status = block_status;
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
        parcel.writeString(address);
        parcel.writeString(media_url);
        parcel.writeString(mobile_status);
        parcel.writeString(admin_id);
        parcel.writeString(user_id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(images);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(pd_status);
        parcel.writeString(status);
        parcel.writeString(last_modified);
        parcel.writeString(dt);
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(mobile);
        parcel.writeString(area);
        parcel.writeString(taluka);
        parcel.writeInt(timestamp);
        parcel.writeString(block_status);
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id='" + id + '\'' +
                ", distance='" + distance + '\'' +
                ", image_id='" + image_id + '\'' +
                ", video='" + video + '\'' +
                ", interest_status='" + interest_status + '\'' +
                ", address='" + address + '\'' +
                ", media_url='" + media_url + '\'' +
                ", mobile_status='" + mobile_status + '\'' +
                ", admin_id='" + admin_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", images='" + images + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", pd_status='" + pd_status + '\'' +
                ", status='" + status + '\'' +
                ", last_modified='" + last_modified + '\'' +
                ", dt='" + dt + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", area='" + area + '\'' +
                ", taluka='" + taluka + '\'' +
                ", timestamp=" + timestamp +
                ", block_status='" + block_status + '\'' +
                '}';
    }
}
