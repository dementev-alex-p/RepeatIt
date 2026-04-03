package com.github.dementev_alex_p.repeatit.commands;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommandParameter {
    private String name;
    private String value;
}
