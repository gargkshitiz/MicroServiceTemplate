package com.org.fms.account.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import com.org.fms.account.exception.AccountNotFoundException;
import com.org.fms.account.model.Account;
import com.org.fms.account.model.AccountRepository;

/**
 * @author Kshitiz Garg
 * For Caching, class needs to be a @Service or @Component
 */
@Service
public class CacheDemoService {
	
	protected Logger logger = LoggerFactory.getLogger(CacheDemoService.class);
	
	@Autowired
	private RedisCacheManager redisCacheManager;
	
	@Autowired
	private AccountRepository accountRepository;

	/**
	 * http://caseyscarborough.com/blog/2014/12/18/caching-data-in-spring-using-redis/
	 * http://docs.spring.io/spring-data/redis/docs/current/reference/html/
	 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html
	 * http://www.javaworld.com/article/3062899/big-data/lightning-fast-nosql-with-spring-data-redis.html
	 * @param partialName
	 * @return
	 * Namespace in redis would be 'accountsByOwnerName'. 
	 * Cache key would be partialName and value would be List<Account> 
	 */
	@Cacheable("accountsByOwnerName")
	public List<Account> getByAccountOwner(String partialName) {
		logger.info("Making database call to fetch accounts for :{}", partialName);
		List<Account> accounts = accountRepository.findByOwnerContainingIgnoreCase(partialName);
		logger.info("accounts-service byOwner() found: {}", accounts);
		if (accounts == null || accounts.isEmpty()){
			throw new AccountNotFoundException(partialName);
		}
		return accounts;
	}
	
	/**
	 * Namespace 'accountsByOwnerName' would be erased from redis
	 * @param partialName
	 */
	@CacheEvict("accountsByOwnerName")
	public void removeCachedAccountByAccountOwner(String partialName) {
		logger.info("removed cached account by account owner for key: {}", partialName);
	}
	
	@CachePut("accountsByOwnerName")
	public List<Account> updateCachedAccountByAccountOwner(String partialName) {
		logger.info("updated cached account by account owner for key: {}", partialName);
		List<Account> accounts = new ArrayList<>();
		Account account = new Account();
		account.setOwner("Demo");
		accounts.add(account);
		return accounts;
	}
	
	public void getByAccountOwnerFromCacheWithoutAnnotation(String partialName) {
		List<Account> accounts;
		logger.info("Calling cache to fetch accounts for :{}", partialName);
    	ValueWrapper valueWrapper = redisCacheManager.getCache("accountsByOwnerName").get(partialName);
    	if(valueWrapper==null){
    		accounts = accountRepository.findByOwnerContainingIgnoreCase(partialName);
			redisCacheManager.getCache("accountsByOwnerName").put(partialName,accounts);
			logger.info("Added accounts:{} in cache",accounts);
    	}
    	else{
    		accounts = (ArrayList<Account>)valueWrapper.get();
    		logger.info("Fetched accounts: {} from cache", accounts);
    	}
	}

}