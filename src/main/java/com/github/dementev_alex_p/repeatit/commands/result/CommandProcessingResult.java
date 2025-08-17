package com.github.dementev_alex_p.repeatit.commands.result;

import com.github.dementev_alex_p.repeatit.commands.CommandEnum;
import lombok.NonNull;
import java.util.List;

public record CommandProcessingResult(@NonNull String message, @NonNull List<CommandLine> availableCommands) {
    public CommandProcessingResult(@NonNull String message) {
        this(message, List.of());
    }
    public CommandProcessingResult(@NonNull String message, CommandLine commandLine) {
        this(message, List.of(commandLine));
    }
}
