package de.zigldrum.ihnn.networking.objects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProposalResponse {

    private final static long serialVersionUID = -7943671069202319658L;

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("error")
    @Expose
    private Boolean error;

    @SerializedName("msg")
    @Expose
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
