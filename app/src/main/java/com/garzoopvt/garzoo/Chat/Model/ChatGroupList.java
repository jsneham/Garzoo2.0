package com.garzoopvt.garzoo.Chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatgrouplist")
public class ChatGroupList implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "pd_id")
    private  String pd_id;

    @ColumnInfo(name = "user_id")
    private  String user_id;

    @ColumnInfo(name = "comment")
    private  String comment;

    @ColumnInfo(name = "file_type")
    private  String file_type;

    @ColumnInfo(name = "status")
    private  String status;

    @ColumnInfo(name = "dt")
    private  String dt;

    @ColumnInfo(name = "last_modified")
    private  String last_modified;

    @ColumnInfo(name = "fname")
    private  String fname;

    @ColumnInfo(name = "lname")
    private  String lname;

    @ColumnInfo(name = "mobile")
    private  String mobile;
    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;

    public ChatGroupList() {
    }

    public ChatGroupList(@NonNull String id, String pd_id, String user_id, String comment, String file_type, String status, String dt, String last_modified, String fname, String lname, String mobile, int timestamp) {
        this.id = id;
        this.pd_id = pd_id;
        this.user_id = user_id;
        this.comment = comment;
        this.file_type = file_type;
        this.status = status;
        this.dt = dt;
        this.last_modified = last_modified;
        this.fname = fname;
        this.lname = lname;
        this.mobile = mobile;
        this.timestamp = timestamp;
    }


    protected ChatGroupList(Parcel in) {
        id = in.readString();
        pd_id = in.readString();
        user_id = in.readString();
        comment = in.readString();
        file_type = in.readString();
        status = in.readString();
        dt = in.readString();
        last_modified = in.readString();
        fname = in.readString();
        lname = in.readString();
        mobile = in.readString();
        timestamp = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(pd_id);
        dest.writeString(user_id);
        dest.writeString(comment);
        dest.writeString(file_type);
        dest.writeString(status);
        dest.writeString(dt);
        dest.writeString(last_modified);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeString(mobile);
        dest.writeInt(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatGroupList> CREATOR = new Creator<ChatGroupList>() {
        @Override
        public ChatGroupList createFromParcel(Parcel in) {
            return new ChatGroupList(in);
        }

        @Override
        public ChatGroupList[] newArray(int size) {
            return new ChatGroupList[size];
        }
    };

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPd_id() {
        return pd_id;
    }

    public void setPd_id(String pd_id) {
        this.pd_id = pd_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
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

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatGroupList{" +
                "id='" + id + '\'' +
                ", pd_uid='" + pd_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", comment='" + comment + '\'' +
                ", file_type='" + file_type + '\'' +
                ", status='" + status + '\'' +
                ", dt='" + dt + '\'' +
                ", last_modified='" + last_modified + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
