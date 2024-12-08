package org.poo.banking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRate {
    private String fromCurrency;
    private String toCurrency;
    private double rate;



}
