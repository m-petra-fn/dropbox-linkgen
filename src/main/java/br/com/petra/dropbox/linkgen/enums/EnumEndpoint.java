package br.com.petra.dropbox.linkgen.enums;

public enum EnumEndpoint {

    CRIAR_SHARE_LINK("sharing/create_shared_link_with_settings");

    private final String url;

    EnumEndpoint(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return getUrl();
    }

    public String getUrl() {
        return url;
    }
}
