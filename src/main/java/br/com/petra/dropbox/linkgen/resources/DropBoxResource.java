package br.com.petra.dropbox.linkgen.resources;

import br.com.petra.dropbox.linkgen.config.ApplicationConstants;
import br.com.petra.dropbox.linkgen.dtos.ResponseCreatedShareLinkDTO;
import br.com.petra.dropbox.linkgen.services.WebClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping(ApplicationConstants.RESOURCE_URL)
public class DropBoxResource {

    @Autowired
    private WebClientService webClientService;

    @GetMapping("listar-arquivos-pasta")
    public ResponseEntity<Void> listarPastas(@RequestParam(required = true) String path, @RequestParam(required = true) String apiKey) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "criar-share-link-file")
    public Mono<ResponseCreatedShareLinkDTO> criarLinkArquivo(@RequestParam(required = true) String path, @RequestParam(required = true) String apiKey) {
        return webClientService.criarLinkShared(path, apiKey);
    }

    @PostMapping(value = "criar-share-link-folder")
    public ResponseEntity<String> criarLinksArquivosDePasta(@RequestParam(required = true) String path, @RequestParam(required = true) String apiKey) throws IOException {
        webClientService.iniciadoProcessoLinks(StringUtils.appendIfMissing(path, "/"), apiKey);
        return ResponseEntity.ok("Processo iniciado");
    }

}
