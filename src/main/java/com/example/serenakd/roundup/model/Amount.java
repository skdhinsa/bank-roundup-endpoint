package com.example.serenakd.roundup.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Amount {
    private String currency;
    private int minorUnits;
}
