package br.com.petra.dropbox.linkgen.services;

import br.com.petra.dropbox.linkgen.config.ApplicationConstants;
import br.com.petra.dropbox.linkgen.dtos.CreateShareLinkDTO;
import br.com.petra.dropbox.linkgen.dtos.ResponseCreatedShareLinkDTO;
import br.com.petra.dropbox.linkgen.dtos.alreadyexistserror.AlreadyExistsErrorDTO;
import br.com.petra.dropbox.linkgen.enums.EnumEndpoint;
import br.com.petra.dropbox.linkgen.utils.DropboxLinkGenUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;

@Service
public class WebClientService {

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            System.out.printf("Request: %s %s%n", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> System.out.printf("%s=%s%n", name, value)));
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.printf("Response: %s", clientResponse.headers().asHttpHeaders().get("property-header"));
            return Mono.just(clientResponse);
        });
    }

    private final WebClient client = WebClient.builder()
            .filter(logRequest())
            .filter(logResponse())
            .baseUrl(ApplicationConstants.BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public Mono<ResponseCreatedShareLinkDTO> criarLinkShared(String path, String apiKey) {
        return client.post()
                .uri(EnumEndpoint.CRIAR_SHARE_LINK.toString())
                .header(HttpHeaders.AUTHORIZATION, prefixarApiKey(apiKey))
                .body(Mono.just(new CreateShareLinkDTO(normalizarPathString(path))).log(), CreateShareLinkDTO.class)
                .retrieve()
                .bodyToMono(ResponseCreatedShareLinkDTO.class);
    }

    private String normalizarPathString(String path) {
        return StringUtils.lowerCase(path);
    }

    public Flux<ResponseCreatedShareLinkDTO> criarLinkSharedAll(String path, String apiKey) throws IOException {
        URI pathDropbox = normalizarPath(path);
        String userHome = System.getProperty("user.home");
        System.out.println("USER HOME: " + userHome);

        URI pastaDropbox = UriComponentsBuilder.newInstance()
                .scheme("file")
                .pathSegment(userHome, "Dropbox", path)
                .build().toUri();

        System.out.println("URI: " + pastaDropbox.getPath());

        File pasta = new File(pastaDropbox);

        System.out.println("PASTA: " + pasta.getAbsolutePath());
        System.out.println("Exists? " + pasta.exists());

        File txtLinkList = new File(pasta.getParentFile().toURI().resolve(pasta.getName() + "-links.txt"));
        System.out.printf("PASTA TEXTLINK: %s%n", txtLinkList.getAbsolutePath());

        FileUtils.writeStringToFile(txtLinkList, StringUtils.EMPTY, Charset.defaultCharset());

        return Flux.from(iterateFiles(pasta, pathDropbox))
                .flatMapSequential(dto ->
                        client.post()
                                .uri(EnumEndpoint.CRIAR_SHARE_LINK.toString())
                                .header(HttpHeaders.AUTHORIZATION, prefixarApiKey(apiKey))
                                .bodyValue(dto)
                                .retrieve()
                                .onStatus(HttpStatus.CONFLICT::equals, ClientResponse::createException)
                                .bodyToMono(ResponseCreatedShareLinkDTO.class)
                                .doOnNext(linkCriado -> escreverLinksEmTxt(linkCriado, txtLinkList))
                                .doOnError(error -> {
                                    if (error instanceof WebClientResponseException) {
                                        WebClientResponseException ex = (WebClientResponseException) error;
                                        HttpStatusCode status = ex.getStatusCode();
                                        System.out.println("Error Status Code: " + status.value());
                                        System.out.println("Error Body: " + ex.getResponseBodyAsString());
                                    } else {
                                        System.out.printf("ERRO MENSAGEM: %s%n", error.getMessage());
                                    }

                                })
                                .onErrorResume(WebClientResponseException.Conflict.class, ex -> {
                                    // Extract AlreadyExistsErrorDTO from the response body
                                    // Convert AlreadyExistsErrorDTO into ResponseCreatedShareLinkDTO
                                    return Mono.justOrEmpty(ex.getResponseBodyAs(AlreadyExistsErrorDTO.class))
                                            .map(ResponseCreatedShareLinkDTO::new)
                                            .doOnNext(linkCriado -> escreverLinksEmTxt(linkCriado, txtLinkList));
                                })
                                .log()
                );
    }

    private void escreverLinksEmTxt(ResponseCreatedShareLinkDTO linkCriado, File txtLinkList) {
        String linkCriadoDownloadable = DropboxLinkGenUtils.getDownloadableLink(linkCriado.getUrl());
        System.out.printf("Link criado para arquivo (%s): %s%n", linkCriado.getName(), linkCriadoDownloadable);
        try {
            FileUtils.writeLines(txtLinkList, Charset.defaultCharset().name(), Collections.singleton(linkCriadoDownloadable), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prefixarApiKey(String apiKey) {
        return ApplicationConstants.BEARER_PREFIX + apiKey;
    }

    private URI normalizarPath(String path) {
        return UriComponentsBuilder.fromPath(StringUtils.lowerCase(path)).build().toUri();
    }

    private Flux<CreateShareLinkDTO> iterateFiles(File pasta, URI pathDropbox) {
        System.out.printf("PATH DROPBOX: %s", pathDropbox);
        return Flux.fromArray(pasta.listFiles())
                .map(file -> new CreateShareLinkDTO(normalizarPathString(pathDropbox + file.getName())))
                .log();
    }
}
