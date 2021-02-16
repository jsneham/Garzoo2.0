package com.garzoopvt.garzoo.Dashboard.Model;

public class HomeGridModelClass {
    private Integer image;
    private Integer name;

    public HomeGridModelClass(Integer image, Integer name) {
        this.image = image;
        this.name = name;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }
}
