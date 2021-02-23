package com.garzoopvt.garzoo.Profile.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "blockedpeople")
public class BlockedPeople implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "block_record_id")
    public String block_record_id;

    @ColumnInfo(name = "fname")
    private String fname;

    @ColumnInfo(name = "lname")
    private String lname;

    @ColumnInfo(name = "flag")
    private  boolean flag;

    @ColumnInfo(name = "status")
    private  String status;

    @ColumnInfo(name = "last_modified")
    private  String last_modified;

    @ColumnInfo(name = "dt")
    private  String dt;

    @ColumnInfo(name = "image")
    private  String image;

    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;

    public BlockedPeople() {
    }

    public BlockedPeople(@NonNull String id, String block_record_id, String fname, String lname, boolean flag, String status, String last_modified, String dt, String image, int timestamp) {
        this.id = id;
        this.block_record_id = block_record_id;
        this.fname = fname;
        this.lname = lname;
        this.flag = flag;
        this.status = status;
        this.last_modified = last_modified;
        this.dt = dt;
        this.image = image;
        this.timestamp = timestamp;
    }

    protected BlockedPeople(Parcel in) {
        id = in.readString();
        block_record_id = in.readString();
        fname = in.readString();
        lname = in.readString();
        flag = in.readByte() != 0;
        status = in.readString();
        last_modified = in.readString();
        dt = in.readString();
        image = in.readString();
        timestamp = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(block_record_id);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeByte((byte) (flag ? 1 : 0));
        dest.writeString(status);
        dest.writeString(last_modified);
        dest.writeString(dt);
        dest.writeString(image);
        dest.writeInt(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BlockedPeople> CREATOR = new Creator<BlockedPeople>() {
        @Override
        public BlockedPeople createFromParcel(Parcel in) {
            return new BlockedPeople(in);
        }

        @Override
        public BlockedPeople[] newArray(int size) {
            return new BlockedPeople[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBlock_record_id() {
        return block_record_id;
    }

    public void setBlock_record_id(String block_record_id) {
        this.block_record_id = block_record_id;
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
    public String toString() {
        return "BlockedPeople{" +
                "id='" + id + '\'' +
                ", block_record_id='" + block_record_id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", flag=" + flag +
                ", status='" + status + '\'' +
                ", last_modified='" + last_modified + '\'' +
                ", dt='" + dt + '\'' +
                ", image='" + image + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
