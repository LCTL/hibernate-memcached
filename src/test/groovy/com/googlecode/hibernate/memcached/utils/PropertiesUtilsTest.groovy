package com.googlecode.hibernate.memcached.utils

import com.googlecode.hibernate.memcached.BaseTestCase
import java.util.concurrent.TimeUnit

class PropertiesUtilsTest extends BaseTestCase {

    Properties props;

    protected void setUp() { 
        props = new Properties()
        props.hello = "world"
        props.one = "1"
        props.thisIsTrue = "true"
        props.thisIsFalse = "false"
        props.seconds = "SECONDS"
    }

    void test_strings() {
        assertEquals "world", PropertiesUtils.get(props, "hello")
        assertEquals "world", PropertiesUtils.get(props, "hello", "blah")
        assertEquals "default", PropertiesUtils.get(props, "nothing", "default")
    }

    void test_boolean() {
        assertFalse PropertiesUtils.getBoolean(props, "blah", false)
        assertTrue PropertiesUtils.getBoolean(props, "blah", true)
        assertTrue PropertiesUtils.getBoolean(props, "thisIsTrue", false)
        assertFalse PropertiesUtils.getBoolean(props, "thisIsFalse", true)

        //Boolean.parseBoolean returns false when it can't parse the value
        assertFalse PropertiesUtils.getBoolean(props, "hello", true)
    }

    void test_long() {
        assertEquals 1L, PropertiesUtils.getLong(props, "one", 10)
        assertEquals 10L, PropertiesUtils.getLong(props, "nothing", 10)

        shouldFail(NumberFormatException) {
            PropertiesUtils.getLong(props, "hello", 10)
        }
    }

    void test_int() {
        assertEquals 1, PropertiesUtils.getInt(props, "one", 10)
        assertEquals 10, PropertiesUtils.getInt(props, "nothing", 10)

        shouldFail(NumberFormatException) {
            PropertiesUtils.getInt(props, "hello", 10)
        }
    }

    void test_enum() {
        assertEquals TimeUnit.SECONDS, PropertiesUtils.getEnum(props, "seconds", TimeUnit, TimeUnit.NANOSECONDS)
        assertEquals TimeUnit.NANOSECONDS, PropertiesUtils.getEnum(props, "nothing", TimeUnit, TimeUnit.NANOSECONDS)

        shouldFail(IllegalArgumentException) {
            PropertiesUtils.getEnum(props, "hello", TimeUnit, TimeUnit.NANOSECONDS)
        }

    }

    void test_find_values() {
        assertNull PropertiesUtils.findValue(props, "this", "does", "not", "exist")
        assertEquals "world", PropertiesUtils.findValue(props, "this", "does", "not", "exist", "hello")
    }

}