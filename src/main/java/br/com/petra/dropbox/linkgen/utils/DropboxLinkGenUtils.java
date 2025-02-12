package br.com.petra.dropbox.linkgen.utils;

import org.apache.commons.lang3.StringUtils;

public abstract class DropboxLinkGenUtils {


    public static String getDownloadableLink(String linkCriado) {
        return StringUtils.replace(linkCriado, "dl=0", "dl=1");
    }

}
