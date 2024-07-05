package xyz.xremote.models.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LinkItem implements Serializable {
    @SerializedName("NAME")
    private String name;

    @SerializedName("LINK_PARTS")
    private List<String> linkPartsList;

    @SerializedName("IS_ALTERNATIVE")
    private boolean isAlternativeLink;
}
