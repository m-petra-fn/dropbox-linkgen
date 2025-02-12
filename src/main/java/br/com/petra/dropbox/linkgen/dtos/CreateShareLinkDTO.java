package br.com.petra.dropbox.linkgen.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

public class CreateShareLinkDTO {

    private String path;

    public CreateShareLinkDTO(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateShareLinkDTO(URI path) {
        this.path = path.getPath();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
