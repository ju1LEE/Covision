package com.activiti.service.activiti.persistent;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.activiti.domain.PersistentToken;
import com.activiti.repository.PersistentTokenRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author Joram Barrez
 */
@Service
@Transactional
public class PersistentTokenServiceImpl implements PersistentTokenService {

	private static final Logger logger = LoggerFactory.getLogger(PersistentTokenServiceImpl.class);

	private static final int DEFAULT_SERIES_LENGTH = 16;

	private static final int DEFAULT_TOKEN_LENGTH = 16;

	private SecureRandom random;

	@Autowired
	private Environment environment;

	@Autowired
	private PersistentTokenRepository persistentTokenRepository;

	// Caching the persistent tokens to avoid hitting the database too often (eg when doing multiple requests at the same time)
	// (This happens a lot, when the page consists of multiple requests)
	private LoadingCache<String, PersistentToken> tokenCache;

	public PersistentTokenServiceImpl() {
		random = new SecureRandom();
	}

	@PostConstruct
	protected void initTokenCache() {
		Long maxSize = environment.getProperty("cache.login-users.max.size", Long.class);
		Long maxAge = environment.getProperty("cache.login-users.max.age", Long.class);
		tokenCache = CacheBuilder.newBuilder().maximumSize(maxSize != null ? maxSize : 2048).expireAfterWrite(maxAge != null ? maxAge : 30, TimeUnit.SECONDS).recordStats()
				.build(new CacheLoader<String, PersistentToken>() {

					public PersistentToken load(final String tokenId) throws Exception {
						PersistentToken persistentToken = persistentTokenRepository.findOne(tokenId);
						if (persistentToken != null) {
							return persistentToken;
						} else {
							throw new PersistentTokenNotFoundException();
						}
					}

				});
	}

	@Override
	public PersistentToken saveAndFlush(PersistentToken persistentToken) {
		return persistentTokenRepository.saveAndFlush(persistentToken);
	}

	@Override
	public void delete(PersistentToken persistentToken) {
		tokenCache.invalidate(persistentToken);
		persistentTokenRepository.delete(persistentToken);
	}

	@Override
	public PersistentToken getPersistentToken(String tokenId) {
		return getPersistentToken(tokenId, false);
	}

	@Override
	public PersistentToken getPersistentToken(String tokenId, boolean invalidateCacheEntry) {

		if (invalidateCacheEntry) {
			tokenCache.invalidate(tokenId);
		}

		try {
			return tokenCache.get(tokenId);
		} catch (ExecutionException e) {
			return null;
		} catch (UncheckedExecutionException e) {
			return null;
		}
	}

	private String generateSeriesData() {
		byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
		random.nextBytes(newSeries);
		return new String(Base64.encode(newSeries));
	}

	private String generateTokenData() {
		byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
		random.nextBytes(newToken);
		return new String(Base64.encode(newToken));
	}

	@Override
	public PersistentToken createToken(String username, String remoteAddress, String userAgent) {

		PersistentToken token = new PersistentToken();
		token.setSeries(generateSeriesData());
		token.setUser(username);
		token.setTokenValue(generateTokenData());
		token.setTokenDate(new Date());
		token.setIpAddress(remoteAddress);
		token.setUserAgent(userAgent);
		try {
			saveAndFlush(token);
			return token;
		} catch (DataAccessException e) {
			logger.error("Failed to save persistent token ", e);
			return token;
		}
	}

	// Just helper exception class for handling null values
	private static class PersistentTokenNotFoundException extends RuntimeException {

	}

}
