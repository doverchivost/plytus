package com.plytus.plytus;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.plytus.plytus.repository")
@EntityScan("com.plytus.plytus.model")
public class PlytusApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlytusApplication.class, args);
		TelegaBot.run();
	}

}
