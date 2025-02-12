package br.com.petra.dropbox.linkgen.dtos;

import br.com.petra.dropbox.linkgen.dtos.alreadyexistserror.AlreadyExistsErrorDTO;
import br.com.petra.dropbox.linkgen.dtos.alreadyexistserror.Metadata;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseCreatedShareLinkDTO {

    private String id;
    private String name;
    private String url;
    private boolean alreadyExists = false;

    @JsonProperty("path_lower")
    private String path;

    @JsonProperty(".tag")
    private String tag;
    private Long size;

    public ResponseCreatedShareLinkDTO(AlreadyExistsErrorDTO alreadyExistsErrorDTO) {
        Metadata metadata = alreadyExistsErrorDTO.getError().getShared_link_already_exists().getMetadata();

        this.alreadyExists = true;
        this.id = metadata.getId();
        this.name = metadata.getName();
        this.path = metadata.getPath_lower();
        this.size = metadata.getSize();
        this.tag = metadata.getTag();
        this.url = metadata.getUrl();
    }

    public String getName() {
        return name;
    }

    public boolean isAlreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(boolean alreadyExists) {
        this.alreadyExists = alreadyExists;
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
