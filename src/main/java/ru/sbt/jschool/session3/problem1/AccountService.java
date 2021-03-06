package ru.sbt.jschool.session3.problem1;

import java.util.Collection;

/**
 */
public interface AccountService {
    Result create(long clientID, long accountID, float initialBalance, Currency currency);

    Collection<Account> findForClient(long clientID);

    Account find(long accountID);

    Result doPayment(Payment payment);
}
