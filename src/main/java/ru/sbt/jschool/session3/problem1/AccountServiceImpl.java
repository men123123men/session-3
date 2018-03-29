package ru.sbt.jschool.session3.problem1;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 */
public class AccountServiceImpl implements AccountService {
    protected FraudMonitoring fraudMonitoring;

    private Map<Long,Set<Long>> clientAccountIDs = new HashMap<>(); // clientID    -> accountIDs
    private Map<Long,Account> accounts = new HashMap<>();           // accountID   -> Account
    private Map<Long,Payment> payments = new HashMap<>();           // operationID -> Payment


    public AccountServiceImpl(FraudMonitoring fraudMonitoring) {
        this.fraudMonitoring = fraudMonitoring;
    }

    @Override public Result create(long clientID, long accountID, float initialBalance, Currency currency) {
        if (fraudMonitoring.check(clientID))
            return Result.FRAUD;

        if (Objects.nonNull(find(accountID)))
            return Result.ALREADY_EXISTS;

        clientAccountIDs.putIfAbsent(clientID,new HashSet<>());
        clientAccountIDs.get(clientID).add(accountID);

        accounts.put(accountID,new Account(clientID, accountID, currency, initialBalance));

        return Result.OK;
    }

    @Override public List<Account> findForClient(long clientID) {
        return clientAccountIDs.get(clientID).stream()
                .map(accounts::get)
                .collect(Collectors.toList());
    }

    @Override public Account find(long accountID) {
        return accounts.get(accountID);
    }

    @Override public Result doPayment(Payment payment) {
        long operationID = payment.getOperationID();
        long paymentID   = payment.getPayerID();
        long recipientID = payment.getRecipientID();
        float amount     = payment.getAmount();

        Account payerAccount     = find(payment.getPayerAccountID());
        Account recipientAccount = find(payment.getRecipientAccountID());

        if (fraudMonitoring.check(paymentID))
            return Result.FRAUD;

        if (payments.containsKey(operationID))
            return Result.ALREADY_EXISTS;

        if (Objects.isNull(payerAccount) || !isPayerIdExists(paymentID))
            return Result.PAYER_NOT_FOUND;
        float payerBalance = payerAccount.getBalance();

        if (payerBalance < amount)
            return Result.INSUFFICIENT_FUNDS;

        if (Objects.isNull(recipientAccount) || !isPayerIdExists(recipientID))
            return Result.RECIPIENT_NOT_FOUND;
        float recipientBalance = recipientAccount.getBalance();


        payments.put(payment.getOperationID(), payment);

        Currency payerCurrency     = payerAccount.getCurrency();
        Currency recipientCurrency = recipientAccount.getCurrency();

        float addToRecipientBalance =
                (payerCurrency == recipientCurrency) ? amount : payerCurrency.to(amount, recipientCurrency);

        payerAccount.setBalance(payerBalance - amount);
        recipientAccount.setBalance(recipientBalance + addToRecipientBalance);

        return Result.OK;
    }

    private boolean isPayerIdExists(long payerID) {
        return clientAccountIDs.containsKey(payerID);
    }
}