package com.ayoubnachti.lets_play;

import org.springframework.boot.SpringApplication;

public class TestLetsPlayApplication {

	public static void main(String[] args) {
		SpringApplication.from(LetsPlayApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
