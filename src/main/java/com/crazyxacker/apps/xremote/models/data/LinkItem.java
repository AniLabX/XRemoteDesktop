package com.crazyxacker.apps.xremote.models.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LinkItem implements Serializable {
    private String name;
    private List<String> linkPartsList;
}
