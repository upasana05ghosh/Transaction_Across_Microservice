package com.example.book.movie.api;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.book.movie.entity.BookingInfo;
import com.example.book.movie.transactional.TransactionalExecution;

@Component
public class PaymentRestApi {

	private final RestTemplate restTemplate;

	@Autowired
	public PaymentRestApi(RestTemplate restTemplate) {
		this.restTemplate = Objects.requireNonNull(restTemplate, "resttemplate can't be null");
	}

	@TransactionalExecution
	public ResponseEntity<BookingInfo> processPayment(BookingInfo info) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("IS_TRANSACTIONAL", "true");
		HttpEntity<BookingInfo> entity = new HttpEntity<BookingInfo>(info, headers);

		return restTemplate.exchange("http://localhost:8080/process/payment", HttpMethod.POST, entity,
				BookingInfo.class);
	}

}
