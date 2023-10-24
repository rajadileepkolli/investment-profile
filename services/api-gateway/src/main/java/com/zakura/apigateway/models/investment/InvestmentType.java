/* Licensed under Apache-2.0 2021-2022 */
package com.zakura.apigateway.models.investment;

import lombok.Getter;

@Getter
public enum InvestmentType {
    STOCK("Stock"),
    MUTUAL_FUND("Mutual Fund"),
    FIXED_DEPOSIT("Fix Deposit");

    private final String investment;

    InvestmentType(String investment) {
        this.investment = investment;
    }
}
