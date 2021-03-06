package com.example.book.movie.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestBean {

	@Bean
    public RestTemplate configureTempalte() {
        return new RestTemplate();
    }

}
