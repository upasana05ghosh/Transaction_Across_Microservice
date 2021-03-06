package com.example.book.movie.transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionManger {

	private final RequestIdHolder holder;

	@Autowired
	public TransactionManger(RequestIdHolder holder) {
		this.holder = holder;
	}

	public Manager begin() {
		return new Manager();
	}

	public class Manager {

		private List<String> requestIds;

		private Manager() {
			this.requestIds = new ArrayList<>();
		}

		public <T> T executeRequest(Callable<ResponseEntity<T>> request) throws Exception {
			ResponseEntity<T> response = request.call();
			HttpHeaders responseHeaders = response.getHeaders();
			requestIds.add(responseHeaders.get("REST_REQUEST_ID").get(0));
			return response.getBody();
		}

		public synchronized void commit() throws InterruptedException, ExecutionException {
			for (String requestId : requestIds) {
				holder.commit(requestId);
			}
			this.requestIds.clear();
		}

		public synchronized void rollback() throws InterruptedException, ExecutionException {
			for (String requestId : requestIds) {
				holder.rollback(requestId);
			}
			this.requestIds.clear();
		}
	}

}
