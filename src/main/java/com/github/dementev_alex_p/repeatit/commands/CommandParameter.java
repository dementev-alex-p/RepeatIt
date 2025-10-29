package com.github.dementev_alex_p.repeatit.commands;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommandParameter {
    private final String name;
    private final String value;
}
