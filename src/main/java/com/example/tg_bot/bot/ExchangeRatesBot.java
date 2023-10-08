package com.example.tg_bot.bot;

import com.example.tg_bot.exception.ServiceException;
import com.example.tg_bot.service.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    private static final String START="/start";
    private static final String USD="/usd";
    private static final String EUR="/eur";
    private static final String HELP="/help";

    @Autowired
    private ExchangeRatesService exchangeRateService;
    public ExchangeRatesBot(@Value("${bot.token}") String botToken){
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }
        var message=update.getMessage().getText();
        var chatId=update.getMessage().getChatId();
        switch(message){
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId );
        }
    }
    @Override
    public String getBotUsername() {
        return "hiuston_exchange_bot";
    }

    private void startCommand(Long chatId, String userName){
        var text= """
                Hello to bot, %s!
                
                Here you can find out about currency,
                
                You can use this commands:
                /usd - dollar currency
                /eur - euro currency
                
                For any help:
                /help
                """;
        var formattedText=String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId){
        String formattedText;
        try{
            var usd=exchangeRateService.getUSDExchangeRate();
            var text="Dollar currency on %s is %s rubles";
            formattedText=String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            LOG.error("error on reciving dollar curency", e);
            formattedText="shit happens";
        }
        sendMessage(chatId, formattedText);
    }
    private void eurCommand(Long chatId){
        String formattedText;
        try{
            var eur=exchangeRateService.getEURExchangeRate();
            var text="Euro currency on %s is %s rubles";
            formattedText=String.format(text, LocalDate.now(), eur);
        } catch (ServiceException e) {
            LOG.error("error on reciving euro curency", e);
            formattedText="shit happens for euros";
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId){
        var text= """
                Here you can find out about currency,
                
                You can use this commands:
                /usd - dollar currency
                /eur - euro currency
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId){
        var text="Cant find out command";
        sendMessage(chatId, text);
    }
    private void sendMessage(Long chatId, String text){
        var chatIdStr=String.valueOf(chatId);
        var sendMessage=new SendMessage(chatIdStr, text);
        try{
            execute(sendMessage);
        }catch (TelegramApiException e){
            LOG.error("Sending error", e);
        }
    }

}












