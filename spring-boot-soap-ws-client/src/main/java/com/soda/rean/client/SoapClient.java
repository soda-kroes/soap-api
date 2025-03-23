package com.soda.rean.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soda.rean.soap.api.loaneligibility.AcknowLedgement;
import com.soda.rean.soap.api.loaneligibility.CustomerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.apache.catalina.manager.JspHelper.escapeXml;

@Service
public class SoapClient {

    // Logger for logging SOAP request/response details and errors
    private static final Logger log = LoggerFactory.getLogger(SoapClient.class);

    // WebClient instance to send HTTP requests
    private final WebClient webClient;

    // Jaxb2Marshaller for converting XML to Java objects
    @Autowired
    private Jaxb2Marshaller marshaller;

    @Autowired
    public SoapClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/ws")  // Replace with actual SOAP endpoint
                .build();
    }

    public AcknowLedgement getLoanStatus(CustomerRequest request) {
        try {
            // Construct the SOAP request payload as an XML string using the helper function
            String soapRequest = createSoapRequest(request);

            log.info("SOAP Request: {}", soapRequest); // Log the generated SOAP request

            // Send the SOAP request using WebClient and retrieve the response as a string
            String responseXml = webClient.post()
                    .uri("")  // Sending request to the base URL set in the constructor
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE) // Setting header for XML request
                    .bodyValue(soapRequest) // Attaching the SOAP request body
                    .retrieve()
                    .bodyToMono(String.class) // Converting response to a string
                    .block(); // Blocking to wait for response synchronously

            log.info("SOAP Response: {}", responseXml); // Log the received SOAP response

            // Parse the SOAP response and extract only the Body
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage(null, new ByteArrayInputStream(responseXml.getBytes()));

            SOAPBody body = message.getSOAPBody(); // Extract the SOAP body
            Node firstChild = (Node) body.getFirstChild(); // Get the first child element inside the body

            log.info("soap body: {}", body);
            log.info("First Child: {}", firstChild.getNodeName());

            if (firstChild != null) {
                try {
                    // Convert the extracted XML node to a string format
                    StringWriter writer = new StringWriter();
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.transform(new DOMSource(firstChild), new StreamResult(writer));

                    // Extracted SOAP Body XML as a string
                    String extractedXml = writer.toString();
                    log.info("Extracted SOAP Body XML: {}", extractedXml); // Log the extracted XML

                    // Unmarshal (convert) XML response to AcknowLedgement Java object
                    StringReader reader = new StringReader(extractedXml);
                    AcknowLedgement acknowLedgement = (AcknowLedgement) marshaller.unmarshal(new StreamSource(reader));

                    // Convert Java object to JSON for better readability in logs
                    ObjectMapper objectMapper = new ObjectMapper();
                    log.info("Final AcknowLedgement Response: {}", objectMapper.writeValueAsString(acknowLedgement));

                    return acknowLedgement; // Return the processed response object
                } catch (Exception e) {
                    log.error("Error processing SOAP response", e);
                }
            }

            // If SOAP Body is empty, throw an error
            throw new RuntimeException("SOAP Response Body is empty");

        } catch (Exception e) {
            log.error("Error in SOAP request", e);
            throw new RuntimeException("Failed to process SOAP request", e);
        }
    }


    private String createSoapRequest(CustomerRequest request) {
        // Using StringBuilder for better performance when concatenating large strings
        StringBuilder soapRequest = new StringBuilder();

        soapRequest.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                .append("xmlns:loan=\"http://www.spring.soap.api/loaneligibility\">")
                .append("<soapenv:Header/>")  // Empty SOAP Header
                .append("<soapenv:Body>")     // Start SOAP Body
                .append("<loan:CustomerRequest>")
                .append("<loan:customerName>").append(escapeXml(request.getCustomerName())).append("</loan:customerName>")
                .append("<loan:age>").append(request.getAge()).append("</loan:age>")
                .append("<loan:yearlyIncome>").append(request.getYearlyIncome()).append("</loan:yearlyIncome>")
                .append("<loan:cibiScore>").append(request.getCibiScore()).append("</loan:cibiScore>")
                .append("<loan:employeeMode>").append(request.getEmployeeMode()).append("</loan:employeeMode>")
                .append("</loan:CustomerRequest>")
                .append("</soapenv:Body>")
                .append("</soapenv:Envelope>");

        return soapRequest.toString();
    }


    private String createSoapRequestStyle1(CustomerRequest request) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:loan=\"http://www.spring.soap.api/loaneligibility\">" +
                "<soapenv:Header/>" +  // Empty SOAP Header
                "<soapenv:Body>" +     // Start SOAP Body
                "<loan:CustomerRequest>" +
                "<loan:customerName>" + request.getCustomerName() + "</loan:customerName>" +
                "<loan:age>" + request.getAge() + "</loan:age>" +
                "<loan:yearlyIncome>" + request.getYearlyIncome() + "</loan:yearlyIncome>" +
                "<loan:cibiScore>" + request.getCibiScore() + "</loan:cibiScore>" +
                "<loan:employeeMode>" + request.getEmployeeMode() + "</loan:employeeMode>" +
                "</loan:CustomerRequest>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
    }
}
