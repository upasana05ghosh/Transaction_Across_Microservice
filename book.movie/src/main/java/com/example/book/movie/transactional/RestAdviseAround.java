package com.example.book.movie.transactional;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.core.Ordered;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestAdviseAround {

	ApplicationContext applicationContext;
	private RequestIdHolder holder;

	@Autowired
	public RestAdviseAround(ApplicationContext applicationContext, RequestIdHolder holder) {
		this.applicationContext = applicationContext;
		this.holder = holder;
	}

	private int COUNTDOWN_LATCH_VAL = 1;

	private static final Logger logger = LoggerFactory.getLogger(RestAdviseAround.class);

	@Around("@annotation(TransactionalExecution)")
	public Object executeInRestXATransaction(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		logger.info("Inside around advise for: {}", proceedingJoinPoint.getSignature().toString());

		ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes());
		HttpServletRequest request = requestAttributes.getRequest();

		boolean isTransactional = this.isTransactional(request);
		if (isTransactional) {
			return makeTransactionalRestCall(proceedingJoinPoint, requestAttributes);
		} else {
			return proceedingJoinPoint.proceed();
		}
	}

	private Object makeTransactionalRestCall(ProceedingJoinPoint proceedingJoinPoint,
			ServletRequestAttributes requestAttributes) throws InterruptedException {

		logger.info("Making REST XA call for call: {}", proceedingJoinPoint.getSignature().toString());

		Callable<Object> task = () -> {
			try {
				return proceedingJoinPoint.proceed();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
		CountDownLatch latch = new CountDownLatch(COUNTDOWN_LATCH_VAL);

		TransactionExecutor executor = applicationContext.getBean(TransactionExecutor.class, task, latch);

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		String requestId = executor.getRequestId();

		Future<String> future = executorService.submit(executor, requestId);

		holder.put(requestId, future);

		logger.info("Waiting for the REST call on requestId: {}", requestId);
		latch.await();

		logger.info("Call made on requestId: {}", requestId);

		HttpServletResponse httpResponse = requestAttributes.getResponse();
		httpResponse.setHeader("REST_REQUEST_ID", requestId);

		return executor.getResponse();
	}

	private boolean isTransactional(HttpServletRequest request) {
		//return Boolean.parseBoolean(request.getHeader("IS_TRANSACTIONAL"));
		return true;
	}

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	public void restCallMethod() {
	}

}
