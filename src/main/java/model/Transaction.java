package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Transaction {
    private final long transactionId;
    private final BigDecimal amount;
    private final long fromAccountId;
    private final long toAccountId;

    public long getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public Transaction(long transactionId, BigDecimal amount, long fromAccountId, long toAccountId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", amount=" + amount +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                '}';
    }
}
