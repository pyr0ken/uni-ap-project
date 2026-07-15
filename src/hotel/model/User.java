package hotel.model;

import java.math.BigDecimal;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
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
