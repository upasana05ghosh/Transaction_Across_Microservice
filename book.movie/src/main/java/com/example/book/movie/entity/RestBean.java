package com.example.book.movie.entity;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import com.example.book.movie.transactional.TransactionExecutor;

@Configuration
public class RestBean {

	@Bean
	public RestTemplate configureTempalte() {
		return new RestTemplate();
	}

	@Bean
	@Lazy(value = true)
	@Scope(SCOPE_PROTOTYPE)
	public TransactionExecutor transactionExecutor(Callable<Object> task, CountDownLatch latch) {
		return new TransactionExecutor(task, latch);
	}

}
