package com.example.tg_bot.service;

import com.example.tg_bot.exception.ServiceException;


public interface ExchangeRatesService {

    String  getUSDExchangeRate() throws ServiceException;

    String  getEURExchangeRate() throws ServiceException;

}
