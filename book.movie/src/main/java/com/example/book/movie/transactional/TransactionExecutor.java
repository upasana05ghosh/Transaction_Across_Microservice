package com.example.book.movie.transactional;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.uuid.Generators;

public class TransactionExecutor implements Runnable {

    private Callable<Object> task;
    private CountDownLatch latch;
    private String requestId;
    private Object response;

    private static final Logger logger = LoggerFactory.getLogger(TransactionExecutor.class);

	public TransactionExecutor(Callable<Object> task, CountDownLatch latch) {
		this.task = task;
		this.latch = latch;
		this.requestId = Generators.timeBasedGenerator().generate().toString();
	}

    public String getRequestId() {
        return requestId;
    }

    @Override
    @Transactional
    public void run() {
        synchronized (requestId) {
            try {
            	response = task.call();
                latch.countDown();
                logger.info("Waiting till someone notify or cancel on requestId: {}" + requestId);
                requestId.wait();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object getResponse() {
    	return response;
    }

}
