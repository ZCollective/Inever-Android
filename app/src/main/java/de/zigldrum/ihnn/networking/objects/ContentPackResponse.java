package de.zigldrum.ihnn.networking.objects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentPackResponse {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("error")
    private Boolean error;

    @SerializedName("msg")
    private List<ContentPack> msg = null;

    @NonNull
    public Boolean getSuccess() {
        if (success == null) {
            return Boolean.FALSE;
        }

        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @NonNull
    public Boolean getError() {
        if (error == null) {
            return Boolean.FALSE;
        }

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

    public String getStatus() {
        return "Success:" + getSuccess() + " | Error: " + getError();
    }

}
