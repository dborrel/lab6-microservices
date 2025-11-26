package web.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import web.service.WebAccountsService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Account DTO - used to interact with the {@link WebAccountsService}.
 *
 * @author Paul Chapman
 */
@JsonRootName("Account")
public class Account {

    protected Long id;
    protected String number;
    protected String owner;
    protected BigDecimal balance;

    /**
     * Default constructor for JPA only.
     */
    public Account() {
        balance = BigDecimal.ZERO;
    }

    public long getId() {
        return id;
    }

    /**
     * Set JPA id - for testing and JPA only. Not intended for normal use.
     *
     * @param id The new id.
     */
    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String accountNumber) {
        this.number = accountNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setBalance(BigDecimal value) {
        balance = value;
        balance = balance.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public String toString() {
        return number + " [" + owner + "]: $" + balance;
    }

}
