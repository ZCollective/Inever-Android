package de.zigldrum.ihnn.networking.objects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ProposalResponse {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("error")
    private Boolean error;

    @SerializedName("msg")
    private String msg;

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return "Success:" + getSuccess() + " | Error: " + getError();
    }
}
