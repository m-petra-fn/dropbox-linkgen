package br.com.petra.dropbox.linkgen;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class DropBoxLinkGenApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(DropBoxLinkGenApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		int response = JOptionPane.showConfirmDialog(null, "Do you want to proceed?", "Confirmation Dialog", JOptionPane.YES_NO_OPTION);
	}
}
