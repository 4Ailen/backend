package com.aliens.friendship.global.config.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.aliens.db")
@EnableJpaRepositories(basePackages = "com.aliens.db")
public class JpaConfig {
}
