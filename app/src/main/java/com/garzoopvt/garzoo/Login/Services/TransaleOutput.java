package com.garzoopvt.garzoo.Login.Services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransaleOutput {
    @SerializedName("source")
    @Expose
    public String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @SerializedName("input")
    @Expose
    public String input;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("model")
    @Expose
    public String model;
}
