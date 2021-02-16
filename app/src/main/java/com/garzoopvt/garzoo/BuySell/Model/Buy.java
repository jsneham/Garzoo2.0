package com.garzoopvt.garzoo.BuySell.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;



@Entity(tableName = "buy")
public class Buy implements Parcelable{

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "admin_id")
    private String admin_id;

    @ColumnInfo(name = "category_id")
    private String category_id;

    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private String price;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "last_modified")
    private String last_modified;

    @ColumnInfo(name = "dt")
    private String dt;

    @ColumnInfo(name = "fname")
    private String fname;

    @ColumnInfo(name = "lname")
    private String lname;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "data_type")
    private String data_type;

    @ColumnInfo(name = "listing_status")
    private String listing_status;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "available_status")
    private String available_status;

    @ColumnInfo(name = "mobile")
    private String mobile;

    @ColumnInfo(name = "images")
    private String images;

    @ColumnInfo(name = "distance")
    private String distance;

    @ColumnInfo(name = "interest_status")
    private String interest_status;

    @ColumnInfo(name = "mobile_status")
    private String mobile_status;

    @ColumnInfo(name = "video")
    private  String video;

    @ColumnInfo(name = "media_url")
    private  String media_url;

    @ColumnInfo(name = "area")
    private   String area;

    @ColumnInfo(name = "taluka")
    private  String taluka;

    @ColumnInfo(name = "pd_status")
    private  String pd_status;

    @ColumnInfo(name = "image_id")
    private  String image_id;

    @ColumnInfo(name = "emp_status")
    private  String emp_status;


    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;



    @Ignore
    public Buy() {
    }




    public Buy(@NonNull String id, String admin_id, String category_id, String user_id, String title, String description,
                         String price, String image, String latitude, String longitude, String status, String last_modified,
                         String dt, String fname, String lname, String category, String data_type, String listing_status,
                         String address, String available_status, String mobile, String images, String distance,
                         String interest_status, String mobile_status, String video, String media_url, String area,
                         String taluka, String pd_status, String image_id, String emp_status, int timestamp) {
        this.id = id;
        this.admin_id = admin_id;
        this.category_id = category_id;
        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.last_modified = last_modified;
        this.dt = dt;
        this.fname = fname;
        this.lname = lname;
        this.category = category;
        this.data_type = data_type;
        this.listing_status = listing_status;
        this.address = address;
        this.available_status = available_status;
        this.mobile = mobile;
        this.images = images;
        this.distance = distance;
        this.interest_status = interest_status;
        this.mobile_status = mobile_status;
        this.video = video;
        this.media_url = media_url;
        this.area = area;
        this.taluka = taluka;
        this.pd_status = pd_status;
        this.image_id = image_id;
        this.emp_status = emp_status;
        this.timestamp = timestamp;
    }


    @Ignore
    public Buy(Buy buyList){
        this.id = buyList.id;
        this.admin_id = buyList.admin_id;
        this.category_id = buyList.category_id;
        this.user_id = buyList.user_id;
        this.title = buyList.title;
        this.description = buyList.description;
        this.price = buyList.price;
        this.image = buyList.image;
        this.latitude = buyList.latitude;
        this.longitude = buyList.longitude;
        this.status = buyList.status;
        this.last_modified = buyList.last_modified;
        this.dt = buyList.dt;
        this.fname = buyList.fname;
        this.lname = buyList.lname;
        this.category = buyList.category;
        this.data_type = buyList.data_type;
        this.listing_status = buyList.listing_status;
        this.address = buyList.address;
        this.available_status = buyList.available_status;
        this.mobile =buyList. mobile;
        this.images = buyList.images;
        this.distance = buyList.distance;
        this.interest_status = buyList.interest_status;
        this.mobile_status = buyList.mobile_status;
        this.video = buyList.video;
        this.media_url = buyList.media_url;
        this.area = buyList.area;
        this.taluka = buyList.taluka;
        this.pd_status = buyList.pd_status;
        this.image_id = buyList.image_id;
        this.emp_status = buyList.emp_status;
        this.timestamp = buyList.timestamp;
    }




    protected Buy(Parcel in) {
        id = in.readString();
        admin_id = in.readString();
        category_id = in.readString();
        user_id = in.readString();
        title = in.readString();
        description = in.readString();
        price = in.readString();
        image = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        status = in.readString();
        last_modified = in.readString();
        dt = in.readString();
        fname = in.readString();
        lname = in.readString();
        category = in.readString();
        data_type = in.readString();
        listing_status = in.readString();
        address = in.readString();
        available_status = in.readString();
        mobile = in.readString();
        images = in.readString();
        distance = in.readString();
        interest_status = in.readString();
        mobile_status = in.readString();
        video = in.readString();
        media_url = in.readString();
        area = in.readString();
        taluka = in.readString();
        pd_status = in.readString();
        image_id = in.readString();
        emp_status = in.readString();
        timestamp = in.readInt();
    }

    public static final Parcelable.Creator<Buy> CREATOR = new Parcelable.Creator<Buy>() {
        @Override
        public Buy createFromParcel(Parcel in) {
            return new Buy(in);
        }

        @Override
        public Buy[] newArray(int size) {
            return new Buy[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getListing_status() {
        return listing_status;
    }

    public void setListing_status(String listing_status) {
        this.listing_status = listing_status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvailable_status() {
        return available_status;
    }

    public void setAvailable_status(String available_status) {
        this.available_status = available_status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getInterest_status() {
        return interest_status;
    }

    public void setInterest_status(String interest_status) {
        this.interest_status = interest_status;
    }

    public String getMobile_status() {
        return mobile_status;
    }

    public void setMobile_status(String mobile_status) {
        this.mobile_status = mobile_status;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
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

    public String getPd_status() {
        return pd_status;
    }

    public void setPd_status(String pd_status) {
        this.pd_status = pd_status;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getEmp_status() {
        return emp_status;
    }

    public void setEmp_status(String emp_status) {
        this.emp_status = emp_status;
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
        parcel.writeString(admin_id);
        parcel.writeString(category_id);
        parcel.writeString(user_id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(image);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(status);
        parcel.writeString(last_modified);
        parcel.writeString(dt);
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(category);
        parcel.writeString(data_type);
        parcel.writeString(listing_status);
        parcel.writeString(address);
        parcel.writeString(available_status);
        parcel.writeString(mobile);
        parcel.writeString(images);
        parcel.writeString(distance);
        parcel.writeString(interest_status);
        parcel.writeString(mobile_status);
        parcel.writeString(video);
        parcel.writeString(media_url);
        parcel.writeString(area);
        parcel.writeString(taluka);
        parcel.writeString(pd_status);
        parcel.writeString(image_id);
        parcel.writeString(emp_status);
        parcel.writeInt(timestamp);
    }

    @Override
    public String toString() {
        return "Buy{" +
                "id='" + id + '\'' +
                ", admin_id='" + admin_id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", status='" + status + '\'' +
                ", last_modified='" + last_modified + '\'' +
                ", dt='" + dt + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", category='" + category + '\'' +
                ", data_type='" + data_type + '\'' +
                ", listing_status='" + listing_status + '\'' +
                ", address='" + address + '\'' +
                ", available_status='" + available_status + '\'' +
                ", mobile='" + mobile + '\'' +
                ", images='" + images + '\'' +
                ", distance='" + distance + '\'' +
                ", interest_status='" + interest_status + '\'' +
                ", mobile_status='" + mobile_status + '\'' +
                ", video='" + video + '\'' +
                ", media_url='" + media_url + '\'' +
                ", area='" + area + '\'' +
                ", taluka='" + taluka + '\'' +
                ", pd_status='" + pd_status + '\'' +
                ", image_id='" + image_id + '\'' +
                ", emp_status='" + emp_status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
