package com.gigcred.loans;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.loans.application.LoanService;
import com.gigcred.loans.domain.LoanAgreement;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoanServiceTest {

    private final LoanService loanService = new LoanService();

    @Test
    void generateAndAcceptOffer() {
        List<?> offers = loanService.generateOffers("user-1", 720);
        assertThat(offers).isNotEmpty();
        String offerId = offers.getFirst().offerId();
        LoanAgreement agreement = loanService.acceptOffer(offerId).orElseThrow();
        loanService.markRepaid(agreement.getId());
        assertThat(loanService.findAgreement(agreement.getId())).isPresent().get().extracting(LoanAgreement::getStatus).isEqualTo("REPAID");
    }
}
