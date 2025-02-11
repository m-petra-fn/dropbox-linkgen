package br.com.petra.dropbox.linkgen.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseCreatedShareLinkDTO {

    private String id;
    private String name;
    private String url;

    @JsonProperty("path_lower")
    private String path;

    @JsonProperty(".tag")
    private String tag;

    private Long size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
