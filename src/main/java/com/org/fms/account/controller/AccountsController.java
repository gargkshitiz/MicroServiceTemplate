package com.org.fms.account.controller;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.org.fms.account.exception.AccountNotFoundException;
import com.org.fms.account.model.Account;
import com.org.fms.account.model.AccountRepository;
import com.org.fms.account.service.CacheDemoService;
import com.org.fms.account.service.HysterixDemoService;
import com.org.fms.account.service.KafkaDemoService;

import io.swagger.annotations.ApiOperation;
/**
 * A RESTFul controller for accessing account information.
 * 
 * @author Kshitiz Garg
 */
@RestController
@RequestMapping("/fms/account")
public class AccountsController {

	protected Logger logger = LoggerFactory.getLogger(AccountsController.class);
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CacheDemoService cacheDemoService;
	
	@Autowired
	private HysterixDemoService hysterixDemoService;
	
	@Autowired
	private KafkaDemoService kafkaDemoService;
	
	@RequestMapping(value ="/{accountNumber}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	@ApiOperation(value =  "Accounts GET" , httpMethod = "GET", notes = "Accounts GET notes")
	public AccountResponse byNumber(@PathVariable("accountNumber") String accountNumber) {
		logger.info("accounts-service byNumber() invoked for accountNumber: {}", accountNumber);
		return getByAccountNumber(accountNumber);
	}

	@RequestMapping(value ="/cacheable/owner/{name}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	@ApiOperation(value =  "Accounts GET using cache" , httpMethod = "GET", notes = "AccountsByOwner GET notes")
	public List<AccountResponse> demoCachebyOwner(@PathVariable("name") String partialName) {
		List<Account> accounts = cacheDemoService.getByAccountOwner(partialName);
		List<AccountResponse> accountResponses = new ArrayList<>();
		for(Account a: accounts){
			accountResponses.add(getAccountResponse(a));
		}
		return accountResponses;
	}
	
	@RequestMapping(value ="/cachingWithoutAnnotation/owner/{name}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	@ApiOperation(value =  "Accounts GET using cache" , httpMethod = "GET", notes = "AccountsByOwner GET notes")
	public void demoRemoteCacheFetchByOwner(@PathVariable("name") String partialName) {
		cacheDemoService.getByAccountOwnerFromCacheWithoutAnnotation(partialName);
	}
	
	@RequestMapping(value ="/cacheable/owner/{name}",method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	@ApiOperation(value =  "Accounts DELETE from cache" , httpMethod = "DELETE", notes = "AccountsByOwner delete notes")
	public void removeCachedAccountByAccountOwner(@PathVariable("name") String partialName) {
		cacheDemoService.removeCachedAccountByAccountOwner(partialName);
	}
	
	@RequestMapping(value ="/cacheable/owner/{name}",method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	@ApiOperation(value =  "Accounts update in cache" , httpMethod = "PUT", notes = "AccountsByOwner put notes")
	public void updateCachedAccountByAccountOwner(@PathVariable("name") String partialName) {
		cacheDemoService.updateCachedAccountByAccountOwner(partialName);
	}
	
	@RequestMapping(value="/demoHysterix", method = RequestMethod.GET, produces ={MediaType.TEXT_PLAIN_VALUE})
	@ApiOperation(value =  "Demo Hysterix" , httpMethod = "GET", notes = "Demo Hysterix")
	public @ResponseBody String demoHysterix() {
		logger.info("demo Hysterix");
		String demoHysterixFallBack = hysterixDemoService.demoHysterixFallBack();
		logger.info("Kshitiz-{}", demoHysterixFallBack);
		return demoHysterixFallBack;
		 
	}
	
	@RequestMapping(value="/demoKafka", method = RequestMethod.GET, produces ={MediaType.TEXT_PLAIN_VALUE})
	@ApiOperation(value =  "Demo Kafka" , httpMethod = "GET", notes = "Demo Kafka")
	public void demoKafka() {
		logger.info("demo Kafka");
		kafkaDemoService.demoKafka();
	}
	
	private AccountResponse getByAccountNumber(String accountNumber){
		Account account = accountRepository.findByAccountNumber(accountNumber);
		logger.info("accounts-service byNumber() found: {}", account);

		if (account == null)
			throw new AccountNotFoundException(accountNumber);
		else {
			return getAccountResponse(account);
		}
	}

	private AccountResponse getAccountResponse(Account a) {
		AccountResponse accountResponse = new AccountResponse();
		BeanUtils.copyProperties(a, accountResponse);
		return accountResponse;
	}

}