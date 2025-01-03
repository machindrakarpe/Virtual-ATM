package service;

import domain.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountService {

    private static final Map<String, Account> accounts = new HashMap<>();

    public Optional<Account> getAccount(String accountName) {
        return Optional.ofNullable(accounts.get(accountName));
    }

    public Account createAccount(String accountName) {
        return getAccount(accountName).orElseGet(() -> {
            Account account = new Account(accountName, BigDecimal.ZERO);
            accounts.put(accountName, account);
            return account;
        });
    }
}
