package de.zigldrum.ihnn.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContentPack implements Serializable {

    private final static long serialVersionUID = 1L;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("keywords")
    @Expose
    private String keywords;

    @SerializedName("min_age")
    @Expose
    private Integer minAge;

    @SerializedName("version")
    @Expose
    private Integer version;

    public ContentPack() {
    }

    public ContentPack(int id, String name, String description, String keywords, int minAge, int version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.keywords = keywords;
        this.minAge = minAge;
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "\n------------------------------------------\n" +
                "ID: " + id + "\n" +
                "Name: " + name + "\n" +
                "Descr.: " + description + "\n" +
                "Keywords: " + keywords + "\n" +
                "Min Age: " + minAge + "\n" +
                "Version: " + version + "\n" +
                "\n------------------------------------------\n";
    }
}
