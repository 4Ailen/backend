package com.aliens.friendship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class FriendshipApplication {

	public static void main(String[] args) {
		SpringApplication.run(FriendshipApplication.class, args);
	}

}
