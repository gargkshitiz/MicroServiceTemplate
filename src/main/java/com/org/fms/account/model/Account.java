package com.org.fms.account.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistent account entity with JPA markup. 
 * 
 * @author Kshitiz Garg
 */
@Entity
@Table(name = "Account")
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AccountId", unique = true, nullable = false)
	protected long accountId;

	@Access(AccessType.FIELD)
	@Column(name = "AccountNumber", nullable = false)
	protected String accountNumber;

	@Access(AccessType.FIELD)
	@Column(name = "Owner", nullable = false)
	protected String owner;

	@Access(AccessType.FIELD)
	@Column(name = "Balance", nullable = false)
	protected BigDecimal balance;

	/**
	 * Default constructor for JPA only.
	 */
	public Account() {
		balance = BigDecimal.ZERO;
	}

	// For UTCs
	public Account(long accountId, String accountNumber, String owner, BigDecimal balance) {
		this.accountId = accountId;
		this.accountNumber = accountNumber;
		this.owner = owner;
		this.balance = balance;
	}


	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	public void withdraw(BigDecimal amount) {
		balance.subtract(amount);
	}

	public void deposit(BigDecimal amount) {
		balance.add(amount);
	}

	@Override
	public String toString() {
		return accountNumber + " [" + owner + "]: $" + balance;
	}

	public Long getAccountId() {
		return accountId;
	}


	public String getAccountNumber() {
		return accountNumber;
	}

}