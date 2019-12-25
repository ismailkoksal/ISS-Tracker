
package fr.ismailkoksal.tp04isstracker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssNow {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("iss_position")
    @Expose
    private IssPosition issPosition;
    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public IssPosition getIssPosition() {
        return issPosition;
    }

    public void setIssPosition(IssPosition issPosition) {
        this.issPosition = issPosition;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

}
