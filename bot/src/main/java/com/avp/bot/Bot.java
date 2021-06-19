package com.avp.bot;

import com.avp.core.bean.User;
import com.avp.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
@PropertySource("classpath:application.properties")
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String userName;

    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }
    @Autowired
    private UserService userService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null && update.hasMessage()) {
            Optional<User> optionalUser = userService.findByName(update.getMessage().getText());
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            if (optionalUser.isPresent()) {
                var user = optionalUser.get();
                int id = user.getId();
                sendMessage.setText("ID пользователя: " + id);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            } else {
                sendMessage.setText("Пользователя с таким именем не существует");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
