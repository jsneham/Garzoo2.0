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

//    @ColumnInfo(name = "to_uid")
//    private  String to_uid;

    @ColumnInfo(name = "title")
    private  String title;





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

    public ChatGroup(@NonNull String id, String to_uid, String title, String last_message, String dt, int timestamp) {
        this.id = id;

        this.title = title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    protected ChatGroup(Parcel in) {
        id = in.readString();
        title = in.readString();

        last_message = in.readString();
        dt = in.readString();

        timestamp = in.readInt();
    }

    public static final Creator<ChatUser> CREATOR = new Creator<ChatUser>() {
        @Override
        public ChatUser createFromParcel(Parcel in) {
            return new ChatUser(in);
        }

        @Override
        public ChatUser[] newArray(int size) {
            return new ChatUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);

        parcel.writeString(last_message);
        parcel.writeString(dt);

        parcel.writeInt(timestamp);
    }


    @Override
    public String toString() {
        return "ChatGroup{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", last_message='" + last_message + '\'' +
                ", dt='" + dt + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
