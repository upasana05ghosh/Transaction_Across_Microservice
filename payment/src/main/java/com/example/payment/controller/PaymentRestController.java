package com.example.payment.controller;



import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment.entity.BookingInfo;

@RestController
@RequestMapping(value = "/process", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentRestController {

	@PostMapping(value = "/payment", consumes = MediaType.APPLICATION_JSON_VALUE)
	public BookingInfo processPayment(@RequestBody BookingInfo info) {
		info.setPaymentId(1);
		return info;
	}

    @GetMapping(value = "/test")
    public String test() {
        return "Success!";
    }

}
