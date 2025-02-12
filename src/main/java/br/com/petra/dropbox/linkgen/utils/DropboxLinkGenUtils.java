package br.com.petra.dropbox.linkgen.utils;

import br.com.petra.dropbox.linkgen.config.ApplicationConstants;
import br.com.petra.dropbox.linkgen.dtos.CreateShareLinkDTO;
import br.com.petra.dropbox.linkgen.dtos.ResponseCreatedShareLinkDTO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;

public abstract class DropboxLinkGenUtils {


    public static String getDownloadableLink(String linkCriado) {
        return StringUtils.replace(linkCriado, "&dl=0", "&dl=1");
    }

    public static void escreverLinksEmTxt(ResponseCreatedShareLinkDTO linkCriado, File txtLinkList) {
        String linkCriadoDownloadable = DropboxLinkGenUtils.getDownloadableLink(linkCriado.getUrl());
        System.out.printf("Link criado para arquivo (%s): %s%n", linkCriado.getName(), linkCriadoDownloadable);
        try {
            FileUtils.writeLines(txtLinkList, Charset.defaultCharset().name(), Collections.singleton(linkCriadoDownloadable), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String prefixarApiKey(String apiKey) {
        return ApplicationConstants.BEARER_PREFIX + apiKey;
    }

    public static URI normalizarPath(String path) {
        return UriComponentsBuilder.fromPath(StringUtils.lowerCase(path)).build().toUri();
    }

    public static Flux<CreateShareLinkDTO> iterateFiles(File pasta, URI pathDropbox) {
        System.out.printf("PATH DROPBOX: %s", pathDropbox);
        return Flux.fromArray(pasta.listFiles())
                .map(file -> new CreateShareLinkDTO(normalizarPathString(pathDropbox + file.getName())))
                .log();
    }

    public static String normalizarPathString(String path) {
        return StringUtils.lowerCase(path);
    }

}
