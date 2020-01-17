package de.zigldrum.ihnn.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentPackResponse {

    private final static long serialVersionUID = -7943671069202319658L;

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("error")
    @Expose
    private Boolean error;

    @SerializedName("msg")
    @Expose
    private List<ContentPack> msg = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<ContentPack> getMsg() {
        return msg;
    }

    public void setMsg(List<ContentPack> msg) {
        this.msg = msg;
    }

}
