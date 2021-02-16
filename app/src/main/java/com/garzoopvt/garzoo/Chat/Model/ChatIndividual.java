package com.garzoopvt.garzoo.Chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatindividual")
public class ChatIndividual implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;


    @ColumnInfo(name = "to_uid")
    private  String to_uid;

    @ColumnInfo(name = "from_uid")
    private  String from_uid;

    @ColumnInfo(name = "message")
    private  String message;

    @ColumnInfo(name = "file_type")
    private  String file_type;

    @ColumnInfo(name = "status")
    private  String status;

    @ColumnInfo(name = "dt")
    private  String dt;

    @ColumnInfo(name = "ufname")
    private  String ufname;

    @ColumnInfo(name = "ulname")
    private  String ulname;

    @ColumnInfo(name = "uufname")
    private  String uufname;


    @ColumnInfo(name = "uulname")
    private  String uulname;

    /**
     * Saves current timestamp in **SECONDS**
     */
    @ColumnInfo(name = "timestamp")
    private int timestamp;

    public ChatIndividual() {
    }

    public ChatIndividual(@NonNull String id, String to_uid, String from_uid, String message, String file_type, String status, String dt, String ufname, String ulname, String uufname, String uulname, int timestamp) {
        this.id = id;
        this.to_uid = to_uid;
        this.from_uid = from_uid;
        this.message = message;
        this.file_type = file_type;
        this.status = status;
        this.dt = dt;
        this.ufname = ufname;
        this.ulname = ulname;
        this.uufname = uufname;
        this.uulname = uulname;
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

    public String getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(String from_uid) {
        this.from_uid = from_uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getUfname() {
        return ufname;
    }

    public void setUfname(String ufname) {
        this.ufname = ufname;
    }

    public String getUlname() {
        return ulname;
    }

    public void setUlname(String ulname) {
        this.ulname = ulname;
    }

    public String getUufname() {
        return uufname;
    }

    public void setUufname(String uufname) {
        this.uufname = uufname;
    }

    public String getUulname() {
        return uulname;
    }

    public void setUulname(String uulname) {
        this.uulname = uulname;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }


    protected ChatIndividual(Parcel in) {
        id = in.readString();
        to_uid = in.readString();
        from_uid = in.readString();
        message = in.readString();
        file_type = in.readString();
        status = in.readString();
        dt = in.readString();
        ufname = in.readString();
        ulname = in.readString();
        uufname = in.readString();
        uulname = in.readString();
        timestamp = in.readInt();
    }

    public static final Creator<ChatIndividual> CREATOR = new Creator<ChatIndividual>() {
        @Override
        public ChatIndividual createFromParcel(Parcel in) {
            return new ChatIndividual(in);
        }

        @Override
        public ChatIndividual[] newArray(int size) {
            return new ChatIndividual[size];
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
        parcel.writeString(from_uid);
        parcel.writeString(message);
        parcel.writeString(file_type);
        parcel.writeString(status);
        parcel.writeString(dt);
        parcel.writeString(ufname);
        parcel.writeString(ulname);
        parcel.writeString(uufname);
        parcel.writeString(uulname);
        parcel.writeInt(timestamp);
    }

    @Override
    public String toString() {
        return "ChatIndividual{" +
                "id='" + id + '\'' +
                ", to_uid='" + to_uid + '\'' +
                ", from_uid='" + from_uid + '\'' +
                ", message='" + message + '\'' +
                ", file_type='" + file_type + '\'' +
                ", status='" + status + '\'' +
                ", dt='" + dt + '\'' +
                ", ufname='" + ufname + '\'' +
                ", ulname='" + ulname + '\'' +
                ", uufname='" + uufname + '\'' +
                ", uulname='" + uulname + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
