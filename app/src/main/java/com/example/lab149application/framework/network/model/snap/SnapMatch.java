
package com.example.lab149application.framework.network.model.snap;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SnapMatch {

    @SerializedName("matched")
    @Expose
    private Boolean matched;

    public Boolean getmatched() {
        return matched;
    }

    public void setId(Boolean matched) {
        this.matched = matched;
    }
}
