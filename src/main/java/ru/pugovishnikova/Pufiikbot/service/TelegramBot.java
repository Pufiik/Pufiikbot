package ru.pugovishnikova.Pufiikbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pugovishnikova.Pufiikbot.config.BotConfig;
import ru.pugovishnikova.Pufiikbot.model.User;
import ru.pugovishnikova.Pufiikbot.model.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String helpText = "This bot is created to demonstrated some console functions\n\n" +
            "Commands:\n"+
            "/start - command, which send you welcome message\n" +
            "/help - command, which send you information about all bot-commands\n" +
            "/getdata - command, which send you your personal data\n" +
            "/gdeletedata - command, which help you delete your personal data\n" +
            "/weather - command, which send you information about current weather in different countries\n";

    public TelegramBot (BotConfig config){
        this.config = config;
        List<BotCommand> listOfComands = new ArrayList<>();
        listOfComands.add(new BotCommand("/start","Get welcome message" ));
        listOfComands.add(new BotCommand("/getdata","Get your data"));
        listOfComands.add(new BotCommand("/deletedata","Delete your data"));
        listOfComands.add(new BotCommand("/help","Info about this bot"));
        listOfComands.add(new BotCommand("/weather","Get weather" ));
        try{
            this.execute(new SetMyCommands(listOfComands, new BotCommandScopeDefault(),null));
        } catch(TelegramApiException e){
            log.error("Error occurred in bots List: "+ e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()){
            String textMessage = update.getMessage().getText();
            long chatID = update.getMessage().getChatId();

            switch (textMessage){
                case "/start":
                    registerUser(update.getMessage());
                    startedProcess(chatID, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatID, helpText);
                    break;
                default:
                    sendMessage(chatID, "Sorry, command wasn't recognized");

            }
        }



    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("user saved "+ user);
        }
        else{
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            System.out.println(msg.getChatId());

        }
    }

    public void startedProcess(long chatId, String name){
         String answer = "Hi, " + name + ", nice to see you here!";
         log.info("Answer to user: "+ name +", chatID: " + chatId);
         sendMessage(chatId, answer);
    }
    private void sendMessage(long chatID, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatID));
        message.setText(textToSend);

        try{
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: "+e.getMessage());
        }
    }
}
