package br.com.petra.dropbox.linkgen.resources;

import br.com.petra.dropbox.linkgen.config.ApplicationConstants;
import br.com.petra.dropbox.linkgen.dtos.CreateShareLinkDTO;
import br.com.petra.dropbox.linkgen.dtos.ResponseCreatedShareLinkDTO;
import br.com.petra.dropbox.linkgen.enums.EnumEndpoint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApplicationConstants.RESOURCE_URL)
public class DropBoxResource {

    private final WebClient client = WebClient.builder()
            .baseUrl(ApplicationConstants.BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @GetMapping("listar-arquivos-pasta")
    public ResponseEntity<Void> listarPastas(@RequestParam(required = true) String path, @RequestParam(required = true) String apiKey) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "criar-share-link-file")
    public Mono<ResponseCreatedShareLinkDTO> criarLinkArquivo(@RequestParam(required = true) String path, @RequestParam(required = true) String apiKey) {
        return client.post()
                .uri(EnumEndpoint.CRIAR_SHARE_LINK.toString())
                .header(HttpHeaders.AUTHORIZATION,  ApplicationConstants.BEARER_PREFIX + apiKey)
                .body(Mono.just(new CreateShareLinkDTO(path)), CreateShareLinkDTO.class)
                .retrieve()
                .bodyToMono(ResponseCreatedShareLinkDTO.class);
    }

}
