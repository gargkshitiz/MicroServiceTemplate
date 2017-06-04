package com.org.fms.account.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.org.fms.account.exception.AccountNotFoundException;
import com.org.fms.account.model.Account;
import com.org.fms.account.model.AccountRepository;
import com.org.fms.account.service.CacheDemoService;
import com.org.fms.account.service.HysterixDemoService;
import com.org.fms.account.service.KafkaDemoService;
import com.org.fms.account.controller.AccountResponse;
import com.org.fms.account.controller.AccountsController;

/**
 * @author kgarg
 */
public class AccountsControllerTest {
 
	private static final String KSH = "Ksh";

	private static final long ACCOUNT_ID = 1;

	private static final String ACCOUNT_NUMBER = "74";

	private static final String ACCOUNT_OWNER = "Kshitiz Garg";

	private static final BigDecimal ACCOUNT_BALANCE = new BigDecimal("42.75");

	@InjectMocks
	private AccountsController accountsController;
	
	@Mock
	private AccountRepository accountRepository;
	
	@Mock
	private CacheDemoService cacheDemoService;
	
	@Mock
	private HysterixDemoService hysterixDemoService;
	
	@Mock
	private KafkaDemoService kafkaDemoService;

	private Account account;
	
	private List<Account> accounts = new ArrayList<>();
	
	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		account = new Account(ACCOUNT_ID, ACCOUNT_NUMBER, ACCOUNT_OWNER, ACCOUNT_BALANCE);
		accounts.add(account);
		Mockito.when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(account);
		Mockito.when(cacheDemoService.getByAccountOwner(KSH)).thenReturn(accounts);
	}
	
	@Test
	public void byNumber(){
		AccountResponse accountResponse = accountsController.byNumber(ACCOUNT_NUMBER);
		Assert.assertEquals(ACCOUNT_NUMBER, accountResponse.getAccountNumber());
		Assert.assertEquals(ACCOUNT_ID, accountResponse.getAccountId());
		Assert.assertEquals(ACCOUNT_OWNER, accountResponse.getOwner());
		Assert.assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
	}
	
	@Test
	public void demoCachebyOwner(){
		List<AccountResponse> accountResponses = accountsController.demoCachebyOwner(KSH);
		AccountResponse accountResponse = accountResponses.get(0);
		Assert.assertEquals(ACCOUNT_NUMBER, accountResponse.getAccountNumber());
		Assert.assertEquals(ACCOUNT_ID, accountResponse.getAccountId());
		Assert.assertEquals(ACCOUNT_OWNER, accountResponse.getOwner());
		Assert.assertEquals(ACCOUNT_BALANCE, accountResponse.getBalance());
	}

	@Test(expected=AccountNotFoundException.class)
	public void demoCachebyOwnerWhenAccountNotFound(){
		whenAccountNotFoundInCache();
		accountsController.demoCachebyOwner(KSH);
	}
	
	private void whenAccountNotFoundInCache() {
		Mockito.when(cacheDemoService.getByAccountOwner(KSH)).thenThrow(new AccountNotFoundException(ACCOUNT_NUMBER));
	}

	@Test(expected=AccountNotFoundException.class)
	public void byNumberWhenAccountNotFound() throws Exception{
		whenAccountNotFound();
		accountsController.byNumber(ACCOUNT_NUMBER);
	}
	
	private void whenAccountNotFound() {
		Mockito.when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(null);
	}

}