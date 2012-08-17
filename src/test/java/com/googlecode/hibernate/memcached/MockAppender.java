package com.googlecode.hibernate.memcached;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;

/**
 * @author Ray Krueger
 */
class MockAppender implements Appender<LoggingEvent> {

    private final Logger log = LoggerFactory.getLogger(MockAppender.class);

    String expectedMessage;
    Exception expectedError;
    boolean appenderCalled = false;

    MockAppender(String expectedMessage, Exception expectedError) {
        this.expectedMessage = expectedMessage;
        this.expectedError = expectedError;
    }

    @Override
	public void doAppend(LoggingEvent event) throws LogbackException {
        Assert.assertEquals(expectedMessage, event.getMessage());
        Assert.assertEquals(expectedError.getClass().getName(), event.getThrowableProxy().getClassName());
        appenderCalled = true;
    }

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isStarted() {
		throw new UnsupportedOperationException("isStarted");
	}

	@Override
	public void setContext(Context context) {
		throw new UnsupportedOperationException("setContext");
	}

	@Override
	public Context getContext() {
		throw new UnsupportedOperationException("getContext");
	}

	@Override
	public void addStatus(Status status) {
		throw new UnsupportedOperationException("addStatus");
	}

	@Override
	public void addInfo(String msg) {
		throw new UnsupportedOperationException("addInfo");
	}

	@Override
	public void addInfo(String msg, Throwable ex) {
		throw new UnsupportedOperationException("addInfo");
	}

	@Override
	public void addWarn(String msg) {
		throw new UnsupportedOperationException("addWarn");
	}

	@Override
	public void addWarn(String msg, Throwable ex) {
		throw new UnsupportedOperationException("addWarn");
	}

	@Override
	public void addError(String msg) {
		throw new UnsupportedOperationException("addError");
	}

	@Override
	public void addError(String msg, Throwable ex) {
		throw new UnsupportedOperationException("addError");
	}

	@Override
	public void addFilter(Filter newFilter) {
		throw new UnsupportedOperationException("addFilter");
	}

	@Override
	public void clearAllFilters() {
		throw new UnsupportedOperationException("clearAllFilters");
	}

	@Override
	public List getCopyOfAttachedFiltersList() {
		throw new UnsupportedOperationException("getCopyOfAttachedFiltersList");
	}

	@Override
	public FilterReply getFilterChainDecision(LoggingEvent event) {
		throw new UnsupportedOperationException("getFilterChainDecision");
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("getName");
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("setName");
	}

}