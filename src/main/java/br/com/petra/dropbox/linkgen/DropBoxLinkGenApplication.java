package br.com.petra.dropbox.linkgen;

import br.com.petra.dropbox.linkgen.services.MultiLinkService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class DropBoxLinkGenApplication implements ApplicationRunner {

    private final ApplicationContext applicationContext; // Field to hold the context

    // Constructor injection (recommended):
    public DropBoxLinkGenApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private MultiLinkService multiLinkService;

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DropBoxLinkGenApplication.class);

        builder.headless(false);

        ConfigurableApplicationContext context = builder.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String apiKey = JOptionPane.showInputDialog(
                null,
                "Enter the API Key. You can find it at https://dropbox.github.io/dropbox-api-v2-explorer/#check_app",
                "API Key",
                JOptionPane.QUESTION_MESSAGE
        );

        String path = JOptionPane.showInputDialog(
                null,
                "Enter the path to the folder inside your Dropbox folder. Example: /Imagens/Rotator/Qaresi",
                "Folder Path",
                JOptionPane.QUESTION_MESSAGE
        );

        if (StringUtils.isAnyBlank(apiKey, path)) {
            JOptionPane.showMessageDialog(null, "Either the apiKey or path are blank, please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        multiLinkService.criarLinkSharedAll(StringUtils.appendIfMissing(path, "/"), apiKey, () -> SpringApplication.exit(applicationContext));
    }
}
