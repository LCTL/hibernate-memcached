package com.googlecode.hibernate.memcached.strategy.key;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * @author Ray Krueger
 */
public class Md5KeyStrategy extends DigestKeyStrategy {
  protected String digest(String key) {
    return StringUtils.md5Hex(key);
  }
}
