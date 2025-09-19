package com.gigcred.payments.application;

import com.gigcred.payments.domain.Quote;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final Map<String, Quote> quotes = new ConcurrentHashMap<>();

    public Quote createQuote(String userId, String sourceCurrency, String destinationCurrency, BigDecimal sourceAmount, BigDecimal providerRate, BigDecimal markupPct, BigDecimal platformFeePct) {
        Quote quote = Quote.create(userId, sourceCurrency, destinationCurrency, sourceAmount, providerRate, markupPct, platformFeePct);
        quotes.put(quote.id(), quote);
        return quote;
    }

    public Optional<Quote> findById(String id) {
        return Optional.ofNullable(quotes.get(id));
    }
}
