package org.poo.userutils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    private String cardNumber;
    private boolean isOneTime;
    private boolean isFrozen;
    private int timeStamp;
    private boolean active;
}
