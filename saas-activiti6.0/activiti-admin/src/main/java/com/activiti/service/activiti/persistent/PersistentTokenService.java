package com.activiti.service.activiti.persistent;

import org.activiti.engine.identity.User;

import com.activiti.domain.PersistentToken;

/**
 * @author Joram Barrez
 */
public interface PersistentTokenService {

  PersistentToken getPersistentToken(String tokenId);

  PersistentToken getPersistentToken(String tokenId, boolean invalidateCacheEntry);

  PersistentToken saveAndFlush(PersistentToken persistentToken);

  void delete(PersistentToken persistentToken);

  public PersistentToken createToken(String user, String remoteAddress, String userAgent);

}
