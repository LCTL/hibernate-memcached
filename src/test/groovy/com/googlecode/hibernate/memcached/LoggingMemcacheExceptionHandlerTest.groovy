package com.googlecode.hibernate.memcached

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory
import org.junit.Assert

/**
 * This test is lame, I have no idea what I should do to make it better.
 * @author Ray Krueger
 */
class LoggingMemcacheExceptionHandlerTest extends BaseTestCase {

    LoggingMemcacheExceptionHandler handler
    Logger logger

    protected void setUp() {
        handler = new LoggingMemcacheExceptionHandler()

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = lc.getLogger(LoggingMemcacheExceptionHandler)
        logger.detachAndStopAllAppenders()
    }

    void testDelete() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'delete' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnDelete "blah", exception
        assert appender.appenderCalled
    }

    void testGet() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'get' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnGet "blah", exception
        assert appender.appenderCalled
    }

    void testIncr() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'incr' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnIncr "blah", 10, 20, exception
        assert appender.appenderCalled
    }

    void testSet() {
        Exception exception = new Exception("blah")
        def appender = new MockAppender("Cache 'set' failed for key [blah]", exception)
        logger.addAppender appender
        handler.handleErrorOnSet "blah", 300, new Object(), exception
        assert appender.appenderCalled
    }

}
