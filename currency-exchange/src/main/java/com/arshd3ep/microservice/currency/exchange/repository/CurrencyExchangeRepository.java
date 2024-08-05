package com.arshd3ep.microservice.currency.exchange.repository;

import com.arshd3ep.microservice.currency.exchange.entity.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long> {
    CurrencyExchange findByFromAndTo(String from, String to);
}