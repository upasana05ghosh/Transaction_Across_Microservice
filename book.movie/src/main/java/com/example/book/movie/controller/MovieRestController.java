package com.example.book.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.movie.api.PaymentRestApi;
import com.example.book.movie.entity.BookingInfo;

@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieRestController {


	private final PaymentRestApi paymentRestApi;

	@Autowired
	public MovieRestController(PaymentRestApi paymentRestApi) {
		this.paymentRestApi = paymentRestApi;
	}



	@PostMapping(value = "book", consumes = MediaType.APPLICATION_JSON_VALUE)
	public BookingInfo bookTicket(@RequestBody BookingInfo info) {
		info.setBookingId(123);
		info.setBookingStatus(1);
		info = paymentRestApi.processPayment(info);

		if(info.getPaymentId() != null && info.getPaymentId() == 1) {
			throw new RuntimeException("bla bla");
		}

		return info;
	}

}
