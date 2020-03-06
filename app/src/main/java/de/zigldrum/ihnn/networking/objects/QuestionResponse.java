package de.zigldrum.ihnn.networking.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionResponse {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("error")
    private Boolean error;

    @SerializedName("msg")
    private List<Question> msg = null;

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

    public List<Question> getMsg() {
        return msg;
    }

    public void setMsg(List<Question> msg) {
        this.msg = msg;
    }
}
