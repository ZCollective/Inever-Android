package de.zigldrum.ihnn.networking.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Question {

    @SerializedName("id")
    private Integer id;

    @SerializedName("string")
    private String string;

    @SerializedName("packid")
    private Integer packid;

    public Question() {
    }

    public Question(int id, String string, int packId) {
        this.id = id;
        this.string = string;
        this.packid = packId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Integer getPackid() {
        return packid;
    }

    public void setPackid(Integer packId) {
        this.packid = packId;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass())) {
            return Objects.equals(((Question) o).getId(), this.getId());
        }
        return false;
    }
}
