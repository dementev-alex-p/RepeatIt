package com.github.dementev_alex_p.repeatit.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CommandHandler {
    public CommandEnum getCommand();
    public void handleCommand(AbsSender sender, Update update) throws TelegramApiException;

}
