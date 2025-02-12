package br.com.petra.dropbox.linkgen.services;

import br.com.petra.dropbox.linkgen.config.ApplicationConstants;
import br.com.petra.dropbox.linkgen.dtos.ResponseCreatedShareLinkDTO;
import br.com.petra.dropbox.linkgen.dtos.alreadyexistserror.AlreadyExistsErrorDTO;
import br.com.petra.dropbox.linkgen.enums.EnumEndpoint;
import br.com.petra.dropbox.linkgen.utils.DropboxLinkGenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.List;

@Service
public class MultiLinkService {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final WebClient client = WebClient.builder()
            .baseUrl(ApplicationConstants.BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void criarLinkSharedAll(String path, String apiKey, Runnable closer) throws IOException {
        URI pathDropbox = DropboxLinkGenUtils.normalizarPath(path);
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

        File[] files = ArrayUtils.nullToEmpty(pasta.listFiles(), File[].class);
        int quantidadeArquivos = files.length;

        JFrame loadingWindow = new JFrame("Carregando...");
        JProgressBar progressBar = new JProgressBar(0, quantidadeArquivos); // Valor mínimo e máximo

        loadingWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadingWindow.setLayout(new BorderLayout());

        loadingWindow.add(progressBar, BorderLayout.CENTER);

        loadingWindow.pack();
        loadingWindow.setLocationRelativeTo(null); // Centraliza a janela
        loadingWindow.setVisible(true);

        FileUtils.writeStringToFile(txtLinkList, StringUtils.EMPTY, Charset.defaultCharset());

        List<ResponseCreatedShareLinkDTO> linksGerados = Flux.from(DropboxLinkGenUtils.iterateFiles(files, pathDropbox))
                .delayElements(Duration.ofSeconds(5))
                .flatMapSequential(dto ->
                        client.post()
                                .uri(EnumEndpoint.CRIAR_SHARE_LINK.toString())
                                .header(HttpHeaders.AUTHORIZATION, DropboxLinkGenUtils.prefixarApiKey(apiKey))
                                .bodyValue(dto)
                                .retrieve()
                                .onStatus(HttpStatus.CONFLICT::equals, ClientResponse::createException)
                                .bodyToMono(ResponseCreatedShareLinkDTO.class)
                                .doOnNext(linkCriado -> DropboxLinkGenUtils.escreverLinksEmTxt(linkCriado, txtLinkList))
                                .doOnEach(each -> progressBar.setValue(progressBar.getValue() + 1))
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
                                            .doOnNext(linkCriado -> DropboxLinkGenUtils.escreverLinksEmTxt(linkCriado, txtLinkList));
                                })
                                .log()
                )
                .collectList()
                .block();

        loadingWindow.dispose();

        int optionChosen = JOptionPane.showConfirmDialog(
                null,
                String.format("Process complete, %s links generated. Do you want to generate a JSON report?", CollectionUtils.emptyIfNull(linksGerados).size()),
                "Success",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (optionChosen == JOptionPane.YES_OPTION) {
            String linksGeradosJSON = OBJECT_MAPPER.writeValueAsString(linksGerados);
            File jsonLinkReport = new File(pasta.getParentFile().toURI().resolve(pasta.getName() + "-links-report.json"));
            FileUtils.writeStringToFile(jsonLinkReport, linksGeradosJSON, Charset.defaultCharset());
        }

        closer.run();
    }

}
