package org.poo.banking;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transcation {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private String amount;
    private String transferType;
}
