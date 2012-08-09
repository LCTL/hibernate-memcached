package com.googlecode.hibernate.memcached.strategy.key;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * @author Ray Krueger
 */
public class Sha1KeyStrategy extends DigestKeyStrategy {
  protected String digest(String key) {
    return StringUtils.sha1Hex(key);
  }
}
