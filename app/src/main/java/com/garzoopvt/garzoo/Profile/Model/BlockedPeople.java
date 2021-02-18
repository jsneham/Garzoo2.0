package com.garzoopvt.garzoo.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FilterCategory implements Serializable {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("flag")
    @Expose
    boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public FilterCategory(String id, String name, String status, String last_modified, String dt, String image,boolean flag) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.last_modified = last_modified;
        this.dt = dt;
        this.image = image;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("last_modified")
    @Expose
    String last_modified;

    @SerializedName("dt")
    @Expose
    String dt;

    @SerializedName("image")
    @Expose
    String image;
}
