package com.gigcred.loans.application;

import com.gigcred.loans.domain.LoanAgreement;
import com.gigcred.loans.domain.LoanOffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private final Map<String, LoanOffer> offers = new ConcurrentHashMap<>();
    private final Map<String, LoanAgreement> agreements = new ConcurrentHashMap<>();

    public List<LoanOffer> generateOffers(String userId, int score) {
        List<LoanOffer> newOffers = new ArrayList<>();
        if (score >= 700) {
            newOffers.add(new LoanOffer(UUID.randomUUID().toString(), userId, 50_000L, 5.0, 30, Instant.now().plusSeconds(86_400)));
        }
        newOffers.add(new LoanOffer(UUID.randomUUID().toString(), userId, 20_000L, 8.5, 21, Instant.now().plusSeconds(86_400)));
        newOffers.forEach(offer -> offers.put(offer.offerId(), offer));
        return newOffers;
    }

    public Optional<LoanAgreement> acceptOffer(String offerId) {
        LoanOffer offer = offers.get(offerId);
        if (offer == null) {
            return Optional.empty();
        }
        LoanAgreement agreement = new LoanAgreement(offer.offerId(), offer.userId(), offer.principalCents(), offer.feePct(), offer.tenorDays());
        agreements.put(agreement.getId(), agreement);
        return Optional.of(agreement);
    }

    public Optional<LoanAgreement> findAgreement(String id) {
        return Optional.ofNullable(agreements.get(id));
    }

    public void markRepaid(String id) {
        Optional.ofNullable(agreements.get(id)).ifPresent(LoanAgreement::markRepaid);
    }
}
