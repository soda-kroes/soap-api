package com.soda.rean.endpoint;

import api.soap.spring.loaneligibility.AcknowLedgement;
import api.soap.spring.loaneligibility.CustomerRequest;
import com.soda.rean.service.LoanEligibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class LoanEligibilityEndpoint {
    private static final String NAMESPACE = "http://www.spring.soap.api/loaneligibility";

    @Autowired
    private LoanEligibilityService loanEligibilityService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "CustomerRequest")
    @ResponsePayload
    public AcknowLedgement getLoanStatus(@RequestPayload CustomerRequest request) {
        return loanEligibilityService.checkLoanEligibility(request);
    }
}
