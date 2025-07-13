package com.github.dementev_alex_p.repeatit.cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Card {
    private final long id;
    private final long userId;
    private String name;
    private String description;
}
