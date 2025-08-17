package com.github.dementev_alex_p.repeatit.user_states;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import lombok.Data;

@Data
public class UserState {
    private final long userId;
    private final CommandEnum currentCommand;
    private final AdditionData additionalData; //todo заменить String на POJO
}
