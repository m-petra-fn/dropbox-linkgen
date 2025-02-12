package br.com.petra.dropbox.linkgen.dtos.alreadyexistserror;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {
    public String url;
    public String id;
    public String name;
    public String path_lower;
    public String preview_type;
    public String rev;
    @JsonProperty(".tag")
    public String tag;
    public Long size;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath_lower() {
        return path_lower;
    }

    public void setPath_lower(String path_lower) {
        this.path_lower = path_lower;
    }

    public String getPreview_type() {
        return preview_type;
    }

    public void setPreview_type(String preview_type) {
        this.preview_type = preview_type;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
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
