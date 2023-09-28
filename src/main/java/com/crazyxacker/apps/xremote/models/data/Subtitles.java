package com.crazyxacker.apps.xremote.models.data;

import com.crazyxacker.apps.xremote.utils.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Subtitles implements Serializable {
    public enum SubtitleType { ANY, ASS, SSA, VTT, TTML, SRT }

    private String prioritySubtitles;

    private String vttSubtitles;
    private String assSubtitles;
    private String ssaSubtitles;
    private String srtSubtitles;
    private String ttmlSubtitles;

    public Subtitles(String prioritySubtitles) {
        this.prioritySubtitles = prioritySubtitles;
    }

    public String getPrioritySubtitles() {
        String prioritySubtitles = Optional.ofNullable(this.prioritySubtitles)
                .filter(Utils::isNotEmpty)
                .orElseGet(() -> {
                    if (Utils.isNotEmpty(assSubtitles)) {
                        return assSubtitles;
                    } else if (Utils.isNotEmpty(ssaSubtitles)) {
                        return ssaSubtitles;
                    } else if (Utils.isNotEmpty(vttSubtitles)) {
                        return vttSubtitles;
                    } else if (Utils.isNotEmpty(ttmlSubtitles)) {
                        return ttmlSubtitles;
                    } else if (Utils.isNotEmpty(srtSubtitles)) {
                        return srtSubtitles;
                    }
                    return null;
                });

        return this.prioritySubtitles = prioritySubtitles;
    }

    public String getSubtitlesByType(SubtitleType subtitleType) {
        return switch (subtitleType) {
            case ANY -> getPrioritySubtitles();
            case ASS -> assSubtitles;
            case SSA -> ssaSubtitles;
            case VTT -> vttSubtitles;
            case TTML -> ttmlSubtitles;
            case SRT -> srtSubtitles;
        };
    }
}
