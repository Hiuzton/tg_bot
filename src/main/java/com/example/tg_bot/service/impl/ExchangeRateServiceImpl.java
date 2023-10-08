package com.example.tg_bot.service.impl;

import com.example.tg_bot.client.CbrClient;
import com.example.tg_bot.exception.ServiceException;
import com.example.tg_bot.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.swing.text.Document;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class ExchangeRateServiceImpl implements ExchangeRatesService {

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient client;


    @Override
    public String getUSDExchangeRate() throws ServiceException {
        var xmlOptional = client.getCurrencyRatesXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException("Cant reach xml")
        );
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

   @Override
    public String getEURExchangeRate() throws ServiceException {
        var xmlOptional = client.getCurrencyRatesXML();
        String xml = xmlOptional.orElseThrow(
                () -> new ServiceException("Cant reach xml")
        );
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }


    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("XML error detection", e);
        }
    }

}
















