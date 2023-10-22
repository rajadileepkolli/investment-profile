package com.zakura.stockservice.models;

import lombok.Getter;

@Getter
public enum EInvestmentType {

	STOCK("Stock"), MUTUAL_FUND("Mutual Fund"), FIXED_DEPOSIT("Fix Deposit");

	private final String investment;

	EInvestmentType(String investment) {
		this.investment = investment;
	}

}
