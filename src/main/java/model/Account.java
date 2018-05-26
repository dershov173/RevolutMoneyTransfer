package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final long accountId;
    private final long userId;
    private final BigDecimal amount;

    public Account(long accountId, long userId, BigDecimal amount) {
        this.accountId = accountId;
        this.userId = userId;
        this.amount = amount;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", amount=" + amount +
                '}';
    }
}
