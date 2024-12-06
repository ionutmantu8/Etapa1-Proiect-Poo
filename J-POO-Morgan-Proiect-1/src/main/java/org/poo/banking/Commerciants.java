package org.poo.banking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Commerciants {
    private String commerciantName;
    private double total;
    private String accountThatPayedTheCommerciant;
}
