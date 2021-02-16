package com.garzoopvt.garzoo.Chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatgroup")
public class ChatGroup implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;

     @ColumnInfo(name = "to_uid")
    private  String to_uid;

     @ColumnInfo(name = "fname")
    private  String fname;

     @ColumnInfo(name = "lname")
    private  String lname;

     @ColumnInfo(name = "count_id")
    private  String count_id;

     @ColumnInfo(name = "chat_count")
    private  String chat_count;

     @ColumnInfo(name = "last_message")
    private  String last_message;

     @ColumnInfo(name = "dt")
    private  String dt;

    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;

    public ChatGroup() {
    }

    public ChatGroup(@NonNull String id, String to_uid, String fname, String lname, String count_id, String chat_count, String last_message, String dt, int timestamp) {
        this.id = id;
        this.to_uid = to_uid;
        this.fname = fname;
        this.lname = lname;
        this.count_id = count_id;
        this.chat_count = chat_count;
        this.last_message = last_message;
        this.dt = dt;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(String to_uid) {
        this.to_uid = to_uid;
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

    public String getCount_id() {
        return count_id;
    }

    public void setCount_id(String count_id) {
        this.count_id = count_id;
    }

    public String getChat_count() {
        return chat_count;
    }

    public void setChat_count(String chat_count) {
        this.chat_count = chat_count;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    protected ChatGroup(Parcel in) {
        id = in.readString();
        to_uid = in.readString();
        fname = in.readString();
        lname = in.readString();
        count_id = in.readString();
        chat_count = in.readString();
        last_message = in.readString();
        dt = in.readString();
        timestamp = in.readInt();
    }

    public static final Creator<ChatGroup> CREATOR = new Creator<ChatGroup>() {
        @Override
        public ChatGroup createFromParcel(Parcel in) {
            return new ChatGroup(in);
        }

        @Override
        public ChatGroup[] newArray(int size) {
            return new ChatGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(to_uid);
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(count_id);
        parcel.writeString(chat_count);
        parcel.writeString(last_message);
        parcel.writeString(dt);
        parcel.writeInt(timestamp);
    }

    @Override
    public String toString() {
        return "ChatGroup{" +
                "id='" + id + '\'' +
                ", to_uid='" + to_uid + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", count_id='" + count_id + '\'' +
                ", chat_count='" + chat_count + '\'' +
                ", last_message='" + last_message + '\'' +
                ", dt='" + dt + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
