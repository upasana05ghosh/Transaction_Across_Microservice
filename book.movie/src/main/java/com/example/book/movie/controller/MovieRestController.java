package com.example.book.movie.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.book.movie.api.PaymentRestApi;
import com.example.book.movie.entity.BookingInfo;
import com.example.book.movie.transactional.TransactionManger;
import com.example.book.movie.transactional.TransactionManger.Manager;

@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieRestController {

	private final PaymentRestApi paymentRestApi;
	private final TransactionManger manager;

	@Autowired
	public MovieRestController(PaymentRestApi paymentRestApi, TransactionManger manager) {
		this.paymentRestApi = paymentRestApi;
		this.manager = manager;
	}

	@PostMapping(value = "book", consumes = MediaType.APPLICATION_JSON_VALUE)
	public BookingInfo bookTicket(@RequestBody final BookingInfo info) throws InterruptedException, ExecutionException {
		BookingInfo booking = null;
		info.setBookingId(123);
		info.setBookingStatus(1);
		Manager m = manager.begin();
		try {
			Callable<ResponseEntity<BookingInfo>> paymentRequest = () -> {
				return paymentRestApi.processPayment(info);
			};
			booking = m.executeRequest(paymentRequest);
			if (booking.getPaymentId() == null) {
				throw new RuntimeException("Payment failure. Transaction is rollbacked");
			}
			m.commit();
		} catch (Exception e) {
			e.printStackTrace();
			rollback(m);
		}

		return booking;
	}

	private void rollback(Manager manager) {
		try {
			manager.rollback();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}
