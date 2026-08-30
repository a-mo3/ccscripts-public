package org.dreambot.discordwebhook.pojo;

import com.google.gson.annotations.SerializedName;

public class EmbedPojo {
    @SerializedName("title")
    private String title = "embed title";
    @SerializedName("type")
    private String type = "rich";
    @SerializedName("description")
    private String description = "embed desc.";
    @SerializedName("color")
    private int color = 1127128;
    @SerializedName("fields")
    private FieldPojo[] fields;
    @SerializedName("image")
    private String image;

    public EmbedPojo(String title, String description, FieldPojo[] fields) {
        this.title = title;
        this.description = description;
        this.fields = fields;
    }

    public EmbedPojo() {
    }

    public EmbedPojo(String title, String type, String description, int color, FieldPojo[] fields) {
        this.title = title;
        this.type = type;
        this.description = description;
        this.color = color;
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public EmbedPojo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getType() {
        return type;
    }

    public EmbedPojo setType(String type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EmbedPojo setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getColor() {
        return color;
    }

    public EmbedPojo setColor(int color) {
        this.color = color;
        return this;
    }

    public FieldPojo[] getFields() {
        return fields;
    }

    public EmbedPojo setFields(FieldPojo... fields) {
        this.fields = fields;
        return this;
    }
}
