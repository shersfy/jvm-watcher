package org.shersfy.jwatcher.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="org.shersfy.jwatcher")
public class JWatcherBoot {

	public static void main(String[] args) {
		SpringApplication.run(JWatcherBoot.class, args);
	}

}
