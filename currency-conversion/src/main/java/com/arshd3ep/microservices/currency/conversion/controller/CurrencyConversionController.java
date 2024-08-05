package com.arshd3ep.microservices.currency.conversion.controller;

import com.arshd3ep.microservices.currency.conversion.beans.CurrencyConversion;
import com.arshd3ep.microservices.currency.conversion.proxy.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping("currency-conversion")
public class CurrencyConversionController {

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @GetMapping("/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion getCurrencyConversion(@PathVariable("from") String from,
                                                    @PathVariable("to") String to,
                                                    @PathVariable("quantity") String quantity){
        String port = environment.getProperty("local.server.port");
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion> responseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);
        CurrencyConversion currencyConversion = responseEntity.getBody();
        if(currencyConversion == null) throw new RuntimeException(String.format("Unable to find data for: %s to %s", from, to));
        currencyConversion.setQuantity(new BigDecimal(quantity));
        currencyConversion.setTotalCalculatedAmount(currencyConversion.getQuantity().multiply(currencyConversion.getConversionMultiple()));
        currencyConversion.setEnvironment(port);
        return currencyConversion;
    }
    @GetMapping("/feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion getCurrencyConversionByFeign(@PathVariable("from") String from,
                                                           @PathVariable("to") String to,
                                                           @PathVariable("quantity") String quantity){
        String port = environment.getProperty("local.server.port");
        CurrencyConversion currencyConversion = currencyExchangeProxy.getExchangeValue(from, to);
        if(currencyConversion == null) throw new RuntimeException(String.format("Unable to find data for: %s to %s", from, to));
        currencyConversion.setQuantity(new BigDecimal(quantity));
        currencyConversion.setTotalCalculatedAmount(currencyConversion.getQuantity().multiply(currencyConversion.getConversionMultiple()));
        return currencyConversion;
    }
}
