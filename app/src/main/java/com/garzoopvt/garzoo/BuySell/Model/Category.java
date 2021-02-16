package com.garzoopvt.garzoo.BuySell.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "category")
public class Category implements Parcelable {

    @PrimaryKey
    @NonNull
    String id;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "flag")
    boolean flag;

    @ColumnInfo(name = "status")
    String status;

    @ColumnInfo(name = "last_modified")
    String last_modified;

    @ColumnInfo(name = "dt")
    String dt;

    @ColumnInfo(name = "image")
    String image;

    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;

    public Category() {
    }

    public Category(@NonNull String id, String name, boolean flag, String status, String last_modified, String dt, String image, int timestamp) {
        this.id = id;
        this.name = name;
        this.flag = flag;
        this.status = status;
        this.last_modified = last_modified;
        this.dt = dt;
        this.image = image;
        this.timestamp = timestamp;
    }

    protected Category(Parcel in) {
        id = in.readString();
        name = in.readString();
        flag = in.readByte() != 0;
        status = in.readString();
        last_modified = in.readString();
        dt = in.readString();
        image = in.readString();
        timestamp = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        parcel.writeString(name);
        parcel.writeByte((byte) (flag ? 1 : 0));
        parcel.writeString(status);
        parcel.writeString(last_modified);
        parcel.writeString(dt);
        parcel.writeString(image);
        parcel.writeInt(timestamp);
    }


    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", flag=" + flag +
                ", status='" + status + '\'' +
                ", last_modified='" + last_modified + '\'' +
                ", dt='" + dt + '\'' +
                ", image='" + image + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
