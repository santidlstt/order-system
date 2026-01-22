package com.ordersystem.domain.model.valueobject;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa dinero con monto y moneda
 * Inmutable y sin identidad propia
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Money {
    private BigDecimal amount;
    private String currency;

    /**
     * Crea una instancia de Money en USD
     */
    public static Money usd(BigDecimal amount) {
        return new Money(amount, "USD");
    }

    /**
     * Suma dos cantidades de dinero (deben ser de la misma moneda)
     */
    public Money add(Money other){
        if(!this.currency.equals(other.currency)){
            throw new IllegalArgumentException("No se pueden sumar monedas diferentes.");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Multiplica el monto por una cantidad
     */
    public Money multiply(int quantity){
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
                Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

}
