package com.github.dementev_alex_p.repeatit.commands.result;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public record CommandProcessingResult(@NonNull String message, @NonNull List<CommandLine> availableCommands) {
    public CommandProcessingResult(@NonNull String message) {
        this(message, List.of());
    }
    public CommandProcessingResult(@NonNull String message, CommandLine commandLine) {
        this(message, List.of(commandLine));
    }

    public static CommandProcessingResult createWithVerticalButtons(@NonNull String message, CommandEnum ... commandEnums) {
        final List<CommandLine> lines = Arrays.stream(commandEnums)
                .map(CommandButton::new)
                .map(CommandLine::new)
                .toList();
        return new CommandProcessingResult(message, lines);
    }
}
