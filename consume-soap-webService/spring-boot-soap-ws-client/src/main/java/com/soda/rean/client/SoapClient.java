package com.soda.rean.client;

import com.soda.rean.soap.api.loaneligibility.AcknowLedgement;
import com.soda.rean.soap.api.loaneligibility.CustomerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class SoapClient {
    @Autowired
    private Jaxb2Marshaller marshaller;
    private WebServiceTemplate template;

    public AcknowLedgement getLoanStatus(CustomerRequest request) {
        template = new WebServiceTemplate(marshaller);
        AcknowLedgement acknowLedgement = (AcknowLedgement) template.marshalSendAndReceive("http://localhost:8080/ws", request);
        return acknowLedgement;
    }

}
