package com.soda.rean.controller;

import com.soda.rean.client.SoapClient;
import com.soda.rean.soap.api.loaneligibility.AcknowLedgement;
import com.soda.rean.soap.api.loaneligibility.CustomerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @Autowired
    private SoapClient soapClient;

    @PostMapping("/getLoanStatus")
    public AcknowLedgement invokeSoapClientGetLoanStatus(@RequestBody CustomerRequest request){
        return soapClient.getLoanStatus(request);
    }
}
