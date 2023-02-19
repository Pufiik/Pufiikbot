package ru.pugovishnikova.Pufiikbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data//генерирует везде геттеры и сеттеры
@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;
}
