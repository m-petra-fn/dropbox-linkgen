package br.com.petra.dropbox.linkgen.dtos.alreadyexistserror;

public class SharedLinkAlreadyExists {
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Metadata metadata;
}
