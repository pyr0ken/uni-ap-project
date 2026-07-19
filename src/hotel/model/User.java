package hotel.model;

import java.math.BigDecimal;

public class User {
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private BigDecimal credit;

    public User(String username, String password, String firstName, String lastName, BigDecimal credit) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.credit = credit;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void addCredit(BigDecimal amount) {
        if (amount.signum() > 0) {
            this.credit = this.credit.add(amount);
        }
    }

    public boolean deductCredit(BigDecimal amount) {
        if (amount.signum() > 0 && this.credit.compareTo(amount) >= 0) {
            this.credit = this.credit.subtract(amount);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return username + ";" + firstName + ";" + lastName + ";" + credit;
    }
}
