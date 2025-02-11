package br.com.petra.dropbox.linkgen.dtos;

public class CreateShareLinkDTO {

    private String path;

    public CreateShareLinkDTO(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
