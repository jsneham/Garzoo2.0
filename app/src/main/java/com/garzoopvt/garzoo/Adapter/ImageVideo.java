package com.garzoopvt.garzoo.Adapter;

public class ImageVideo {
    String path,type, image_id;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageVideo(String path, String type, String image_id) {
        this.path = path;
        this.type = type;
        this.image_id = image_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }
}
