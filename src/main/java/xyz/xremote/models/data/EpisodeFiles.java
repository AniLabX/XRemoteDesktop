package xyz.xremote.models.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import xyz.xremote.utils.Utils;

import java.io.Serializable;
import java.util.List;

@Data
public class EpisodeFiles implements Serializable {
    @SerializedName("DEFAULT")
    private String defaultLink;
    @SerializedName("QUALITIES")
    private List<String> qualities;
    @SerializedName("LINK_ITEMS")
    private List<LinkItem> links;
    @SerializedName("ALTERNATIVE_LINK_ITEMS")
    private List<LinkItem> alternativeLinks;
    @SerializedName("SKIP_MIRROR_SEARCH")
    private boolean skipMirrorSearch;
    @SerializedName("HAS_QUALITY_VARIANTS")
    private boolean hasQualityVariants;

    public LinkItem getQualityLink(String qualityName) {
        for (LinkItem item : links) {
            if (item.getName().equals(qualityName)) {
                return item;
            }
        }
        return null;
    }

    public boolean hasLinks() {
        return Utils.isNotEmpty(links);
    }
}
