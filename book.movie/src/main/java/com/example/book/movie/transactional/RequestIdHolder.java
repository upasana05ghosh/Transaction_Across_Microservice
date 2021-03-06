package com.example.book.movie.transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestIdHolder {
	private ConcurrentMap<String, Future<String>> requestIds = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(RequestIdHolder.class);

	public void commit(String requestId) throws InterruptedException, ExecutionException {
		wakeUpThread(requestId);
		this.requestIds.get(requestId).get();
		this.requestIds.remove(requestId);
	}

	public void rollback(String requestId) throws InterruptedException, ExecutionException {
		this.requestIds.get(requestId).cancel(true);
		this.requestIds.remove(requestId);
	}

	private synchronized void wakeUpThread(String requestId) {
		requestId.notify();
	}

	public void put(String requestId, Future<String> future) {
		logger.info("Inserting new request with id: {}", requestId);
		requestIds.put(requestId, future);
	}

}
