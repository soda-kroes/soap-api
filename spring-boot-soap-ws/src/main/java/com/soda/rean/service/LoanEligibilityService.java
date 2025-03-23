package com.soda.rean.service;

import api.soap.spring.loaneligibility.AcknowLedgement;
import api.soap.spring.loaneligibility.CustomerRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanEligibilityService {

    public AcknowLedgement checkLoanEligibility(CustomerRequest request) {
        AcknowLedgement acknowledgement = new AcknowLedgement();
        List<String> mismatchCriterialist = acknowledgement.getCriteriaMismatch();
        if (!(request.getAge() > 30 && request.getAge() <= 60)) {
            mismatchCriterialist.add("Person age is between 30 and 60");
        }
        if (!(request.getYearlyIncome() > 200000)) {
            mismatchCriterialist.add("minimum income should be more than 200000");
        }
        if (!(request.getCibiScore() > 500)) {
            mismatchCriterialist.add("Low CIBIL score please try after month");
        }

        if (mismatchCriterialist.size() > 0) {
            acknowledgement.setApprovedAmount(0);
            acknowledgement.setIsEligible(false);
        } else {
            acknowledgement.setApprovedAmount(500000);
            acknowledgement.setIsEligible(true);
            mismatchCriterialist.clear();
        }
        return acknowledgement;
    }
}
