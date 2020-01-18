package de.zigldrum.ihnn.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Question implements Serializable {

    private static final long serialVersionUid = 1L;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("string")
    @Expose
    private String string;

    @SerializedName("packid")
    @Expose
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
