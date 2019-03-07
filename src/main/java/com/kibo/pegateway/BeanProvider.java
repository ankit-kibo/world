package com.kibo.pegateway;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.kibo.pegateway.config.WorldpayConstants;
import com.mozu.api.contracts.paymentservice.extensibility.v1.CaptureRequest;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayAuthorizationRequest;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayAuthorizeResponse;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayCaptureResponse;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayCreditResponse;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayVoidResponse;
import com.mozu.api.contracts.paymentservice.extensibility.v1.KeyValueTuple;

@Configuration
public class BeanProvider {
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Autowired
	RestTemplate restTemplate;
	
    @Bean
    public BuildProperties getBuildProperties() {
        Properties properties = new Properties();
        BuildProperties buildProperties = new BuildProperties(properties);
        return buildProperties;
    }
    
    private static final Logger logger = Logger.getLogger(BeanProvider.class.getSimpleName());

    @Bean
    public IPeService getPeService() {
    	
        return new IPeService() {
           @Override
           public GatewayAuthorizeResponse authorize(GatewayAuthorizationRequest request) {
            	
        	   String username = null;
        	   String password = null;
        	   String plainCreds = null;
        	   String endpoint = null;
        	   String configuration = null;
        	   GatewayAuthorizeResponse gatewayAuthorizeResponse = null;
        	   
        	try{
        		   
        	   logger.log(Level.INFO,"==inside authorize method implementation=="+request);	   
        	   
        	   if(request != null){
        	    List<KeyValueTuple> configurationList = request.getContext().getSettings();
        	    
        	    if(configurationList != null){
        	     Iterator<KeyValueTuple> itr = configurationList.iterator();
        	    
        	     while(itr.hasNext()){
        	    	KeyValueTuple KeyValueTuple = itr.next();
        	    	String key = KeyValueTuple.getKey();
        	    	
        	    	if(key.equalsIgnoreCase("username")){
           	    	   username = (String) KeyValueTuple.getValue();
           	    	}
        	    	else if(key.equalsIgnoreCase("password")){
           	    	   password = (String) KeyValueTuple.getValue();
           	    	}
        	    	else if(key.equalsIgnoreCase("endpoint")){
           	    	   endpoint = (String) KeyValueTuple.getValue();
           	    	}
        	    	else if(key.equalsIgnoreCase("configuration")){
              	       configuration = (String) KeyValueTuple.getValue();
              	    }
        	       }
        	      }
        	    }
            	
            	gatewayAuthorizeResponse = new  GatewayAuthorizeResponse();
            	logger.log(Level.INFO,"==inside authorize method implementation username=="+username);
            	logger.log(Level.INFO,"==inside authorize method implementation password=="+password);
            	logger.log(Level.INFO,"==inside authorize method implementation endpoint=="+endpoint);
            	logger.log(Level.INFO,"==inside authorize method implementation configuration=="+configuration);
            	
            	if(username != null && password != null)
            	 plainCreds = username + WorldpayConstants.COLON + password;
            	else
                 plainCreds = WorldpayConstants.USERNAME + WorldpayConstants.COLON + WorldpayConstants.PASSWORD;	
            	
            	byte[] plainCredsBytes = plainCreds.getBytes();

            	byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
            	
            	//Added condition for testing of API with dummy data
            	String worldpayRequestXml = request != null ? createWorldpayRequest(request) : createWorldpayRequestWithDummyData();
            	
            	logger.log(Level.INFO,"==worldpayRequestXml=="+worldpayRequestXml);
            	
            	String base64Creds = new String(base64CredsBytes);
            	HttpHeaders headers = new HttpHeaders();
            	headers.add("Authorization", "Basic " + base64Creds);
            	HttpEntity<String> request1 = new HttpEntity<String>(worldpayRequestXml,headers);
            	
            	String url = endpoint != null ? endpoint : WorldpayConstants.ENDPOINT;
            	
            	logger.log(Level.INFO,"==url=="+url);
            	
            	ResponseEntity<String> serviceResponse = restTemplate.postForEntity(url, request1, String.class);
            	
            	logger.log(Level.INFO,"==authorize method response=="+serviceResponse);
            	
            	if(serviceResponse!=null) {
            		
            		StringTokenizer st = new StringTokenizer(serviceResponse.toString(), ",");
            		String response = null;
            		
            		while(st.hasMoreTokens()){
            			response = st.nextToken();
            			if(response.contains("xml")){
            			  Map<String, Object> pareseResponse = getCardAuthResponse(response);
            			  
            			  logger.log(Level.INFO,"==pareseResponse=="+pareseResponse);
                    	  gatewayAuthorizeResponse.setAuthCode(pareseResponse.get(WorldpayConstants.ORDER_CODE) != null ? pareseResponse.get(WorldpayConstants.ORDER_CODE).toString() : "");
                    	  gatewayAuthorizeResponse.setResponseCode(pareseResponse.get(WorldpayConstants.PAYMENT_METHOD) != null ? pareseResponse.get(WorldpayConstants.PAYMENT_METHOD).toString() : "");
                    	  gatewayAuthorizeResponse.setResponseText(pareseResponse.get(WorldpayConstants.AUTH_RESPONSE) != null ? pareseResponse.get(WorldpayConstants.AUTH_RESPONSE).toString() : "");
            			}
            		}
            	 }
        	   }
        	   catch(ResourceAccessException rae){
        		 rae.printStackTrace();
        		 gatewayAuthorizeResponse.setResponseText("Failed");
     		     logger.log(Level.SEVERE,"==ResourceAccessException in authorize method implementation=="+rae.getMessage());
     	       }
        	   catch(Exception e){
        		  e.printStackTrace();
        		  gatewayAuthorizeResponse.setResponseText("Failed");
        		  logger.log(Level.SEVERE,"==Exception in authorize method implementation=="+e.getMessage());
        	   }
            	
             // logger.log(Level.INFO,"==gatewayAuthorizeResponse=="+gatewayAuthorizeResponse.getResponseText());
              return gatewayAuthorizeResponse;
            }

            
			@Override
            public GatewayCaptureResponse capture(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayCreditResponse credit(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayVoidResponse doVoid(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayCaptureResponse authorizeAndCapture(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }
            
        };
    }
    
    /**
     * @param request
     * @return
     * Generate XML request for worldpay authorization call
     */
    private String createWorldpayRequest(GatewayAuthorizationRequest request) {
    	
    	String output = "";
				
    	      try {
					
					logger.log(Level.INFO,"==inside createWorldpayRequest=="+request);	
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.newDocument();
					// root element
					Element rootElement = doc.createElement("paymentService");
					doc.appendChild(rootElement);
					
					//It is static value
					Attr versionAttr = doc.createAttribute("version");
					versionAttr.setValue("1.4");
					rootElement.setAttributeNode(versionAttr);
					
					Attr paymentAttr = doc.createAttribute("merchantCode");
					paymentAttr.setValue(WorldpayConstants.MERCHANT_CODE);
					rootElement.setAttributeNode(paymentAttr);
			        
					Element submit = doc.createElement("submit");
					rootElement.appendChild(submit);

					Element order = doc.createElement("order");
					submit.appendChild(order);
					
					logger.log(Level.INFO,"==inside createWorldpayRequest customerID=="+request.getShopper().getCustomerId());
					
					Attr orderAttr = doc.createAttribute("orderCode");
					orderAttr.setValue(request.getShopper().getCustomerId()+System.currentTimeMillis());
			        order.setAttributeNode(orderAttr);
			        
			        Element description = doc.createElement("description");
			        description.appendChild(doc.createTextNode(WorldpayConstants.SITECODE_US));
			        order.appendChild(description);
			        
			        Element amount = doc.createElement("amount");
			        order.appendChild(amount);
			        
			        // Check about currency code in ML. keep it hardcoded for UK now
			        Attr currencyAttr = doc.createAttribute("currencyCode");
			        currencyAttr.setValue("GBP");
					amount.setAttributeNode(currencyAttr);
					
					Attr exponentAttr = doc.createAttribute("exponent");
					exponentAttr.setValue("2");
					amount.setAttributeNode(exponentAttr);
					
				    Attr valueAttr = doc.createAttribute("value");
				    
				    NumberFormat format = NumberFormat.getInstance();
                    format.setMinimumFractionDigits(2);
                    format.setMaximumFractionDigits(2);
                    format.setGroupingUsed(false);
                    String formattedAmount = format.format(request.getAmount()).replace(".", "");
                    
                    logger.log(Level.INFO,"==inside createWorldpayRequest formattedAmount=="+formattedAmount);
				    
				    valueAttr.setValue(String.valueOf(formattedAmount));
					amount.setAttributeNode(valueAttr);
			        
					 Element paymentDetails = doc.createElement("paymentDetails");
				     order.appendChild(paymentDetails);
				     
				     // It's a dynamic value and keep it hardcoded for CC for now
				     Element paymentMethod = doc.createElement("VISA-SSL");
				     paymentDetails.appendChild(paymentMethod);
				     
				     // Credit card number
				     Element cardNumber = doc.createElement("cardNumber");
				     logger.log(Level.INFO,"==inside createWorldpayRequest cc no.=="+request.getCard().getNumberPart());
				     cardNumber.appendChild(doc.createTextNode(request.getCard().getNumberPart()));
				     paymentMethod.appendChild(cardNumber);
				     
				     Element expiryDate = doc.createElement("expiryDate");
				     paymentMethod.appendChild(expiryDate);
				     
				     Element date = doc.createElement("date");
				     expiryDate.appendChild(date);
				     
				     Attr monthAttr = doc.createAttribute("month");
				     monthAttr.setValue(String.valueOf(request.getCard().getExpireMonth()));
				     date.setAttributeNode(monthAttr);
				     
				     Attr yearAttr = doc.createAttribute("year");
				     yearAttr.setValue(String.valueOf(request.getCard().getExpireYear()));
				     date.setAttributeNode(yearAttr);
				     
				     logger.log(Level.INFO,"==inside createWorldpayRequest cardHolderName=="+request.getCard().getCardHolderName());
				     
				     Element cardHolderName = doc.createElement("cardHolderName");
				     cardHolderName.appendChild(doc.createTextNode(request.getCard().getCardHolderName()));
				    //cardHolderName.appendChild(doc.createTextNode("Test"));
				     paymentMethod.appendChild(cardHolderName);
				     
				     Element cvc = doc.createElement("cvc");
				     cvc.appendChild(doc.createTextNode(request.getCard().getCvv()));
				     paymentMethod.appendChild(cvc);
				     
				     Element cardAddress = doc.createElement("cardAddress");
				     paymentMethod.appendChild(cardAddress);
				     
				     Element address = doc.createElement("address");
				     cardAddress.appendChild(address);
				     
				     
				     if(request.getShopper().getAddress().getLine1() != null){
				      Element address1 = doc.createElement("address1");
				      address1.appendChild(doc.createTextNode(request.getShopper().getAddress().getLine1()));
				      address.appendChild(address1);
				     }
				     
				     if(request.getShopper().getAddress().getLine2() != null){
				      Element address2 = doc.createElement("address2");
				      address2.appendChild(doc.createTextNode(request.getShopper().getAddress().getLine2()));
				      address.appendChild(address2);
				     }
				     
				     if(request.getShopper().getAddress().getLine3() != null){
				      Element address3 = doc.createElement("address3");
				      address3.appendChild(doc.createTextNode(request.getShopper().getAddress().getLine3()));
				      address.appendChild(address3);
				     }
				     
				     if(request.getShopper().getAddress().getPostalCode() != null){
				      Element postalCode = doc.createElement("postalCode");
				      postalCode.appendChild(doc.createTextNode(request.getShopper().getAddress().getPostalCode()));
				      address.appendChild(postalCode);
				     }
				     
				     if(request.getShopper().getAddress().getCity() != null){
				      Element city = doc.createElement("city");
				      city.appendChild(doc.createTextNode(request.getShopper().getAddress().getCity()));
				      address.appendChild(city);
				     }
				     
				     if(request.getShopper().getAddress().getState() != null){
				      Element state = doc.createElement("state");
				      state.appendChild(doc.createTextNode(request.getShopper().getAddress().getState()));
				      address.appendChild(state);
				     }
				     
				     if(request.getShopper().getAddress().getCountry() != null){
				      Element countryCode = doc.createElement("countryCode");
				      countryCode.appendChild(doc.createTextNode(request.getShopper().getAddress().getCountry()));
				      address.appendChild(countryCode);
				     }
				     
				  // There are two methods for phone number
				     String phoneNumber = request.getShopper().getContact().getPhone() != null ? request.getShopper().getContact().getPhone() : request.getShopper().getPhoneNumber();
				     
				     if(phoneNumber != null){
				      Element telephoneNumber = doc.createElement("telephoneNumber");
				      telephoneNumber.appendChild(doc.createTextNode(request.getShopper().getPhoneNumber()));
				      //telephoneNumber.appendChild(doc.createTextNode("1234567890"));
				      address.appendChild(telephoneNumber);
				     }
				     
				     Element session = doc.createElement("session");
				     paymentDetails.appendChild(session);
				     
				     if(request.getShopper().getRequestorIp() != null){
				      Attr shopperIPAddress = doc.createAttribute("shopperIPAddress");
				      shopperIPAddress.setValue(request.getShopper().getRequestorIp());
				      session.setAttributeNode(shopperIPAddress);
				     }
				     
				     // 6383A29AE40E5839DCA4D17EE86A2495.adm-mymms-rev2v162-int001 
				     // pass the custmer id
				     if(request.getShopper().getCustomerId() != null){
				      Attr idAttr = doc.createAttribute("id");
				      idAttr.setValue(request.getShopper().getCustomerId());
				      session.setAttributeNode(idAttr);
				     }
				     
				     Element shopper = doc.createElement("shopper");
				     order.appendChild(shopper);
				     
				     if(request.getShopper().getContact().getEmail() != null){
				      Element shopperEmailAddress = doc.createElement("shopperEmailAddress");
				      shopperEmailAddress.appendChild(doc.createTextNode(request.getShopper().getContact().getEmail()));
				      shopper.appendChild(shopperEmailAddress);
				     }
				     
				     Element browser = doc.createElement("browser");
				     shopper.appendChild(browser);
				     
				     Element acceptHeader = doc.createElement("acceptHeader");
				     acceptHeader.appendChild(doc.createTextNode(WorldpayConstants.ACCEPT_HEADER));
				     browser.appendChild(acceptHeader);
				     
				     if(request.getShopper().getRequestorUserAgent() != null){
				    	 Element userAgentHeader = doc.createElement("userAgentHeader");
						 userAgentHeader.appendChild(doc.createTextNode(request.getShopper().getRequestorUserAgent()));
						 //userAgentHeader.appendChild(doc.createTextNode("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36"));
						 browser.appendChild(userAgentHeader);
				     }
				     
				     Element statementNarrative = doc.createElement("statementNarrative");
				     statementNarrative.appendChild(doc.createTextNode(WorldpayConstants.SITECODE_US));
				     order.appendChild(statementNarrative);
				     
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
			        
					try {
						  Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				        DOMImplementation domImpl = doc.getImplementation();
				        DocumentType doctype = domImpl.createDocumentType(WorldpayConstants.QUALIFIED_NAME,
				        		WorldpayConstants.PUBLICID,WorldpayConstants.SYSTEMID);
				        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
				        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
				         try {
				        	  StringWriter writer = new StringWriter();
						        transformer.transform(new DOMSource(doc), new StreamResult(writer));
						         output = writer.getBuffer().toString();
						} catch (TransformerException e) {
							e.printStackTrace();
							logger.log(Level.SEVERE, "==TransformerException in createWorldpayRequest==", e.getMessage());
						}
					} catch (TransformerConfigurationException e) {
						e.printStackTrace();
						logger.log(Level.SEVERE, "==TransformerConfigurationException in createWorldpayRequest==", e.getMessage());
					}
			         
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "==ParserConfigurationException in createWorldpayRequest==", e.getMessage());
				}
    	        
		return output;
	}
    
    /**
     * @param request
     * @return
     * Generate XML request(hardcoded data) for worldpay authorization call
     */
    private String createWorldpayRequestWithDummyData() {
    	
    	String output = "";
				
    	           try {
					
					logger.log(Level.INFO,"==inside createWorldpayRequestWithDummyData==");	
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.newDocument();
					// root element
					Element rootElement = doc.createElement("paymentService");
					doc.appendChild(rootElement);
					
					Attr versionAttr = doc.createAttribute("version");
					versionAttr.setValue("1.4");
					rootElement.setAttributeNode(versionAttr);
					
					Attr paymentAttr = doc.createAttribute("merchantCode");
					paymentAttr.setValue(WorldpayConstants.MERCHANT_CODE);
					rootElement.setAttributeNode(paymentAttr);
			        
					Element submit = doc.createElement("submit");
					rootElement.appendChild(submit);

					Element order = doc.createElement("order");
					submit.appendChild(order);
					
					Attr orderAttr = doc.createAttribute("orderCode");
					orderAttr.setValue("25262314380969811345678907888836"+System.currentTimeMillis());
			        order.setAttributeNode(orderAttr);
			        
			        Element description = doc.createElement("description");
			        description.appendChild(doc.createTextNode(WorldpayConstants.SITECODE_US));
			        order.appendChild(description);
			        
			        Element amount = doc.createElement("amount");
			        order.appendChild(amount);
			        
			        Attr currencyAttr = doc.createAttribute("currencyCode");
			        currencyAttr.setValue("GBP");
					amount.setAttributeNode(currencyAttr);
					
					Attr exponentAttr = doc.createAttribute("exponent");
					exponentAttr.setValue("2");
					amount.setAttributeNode(exponentAttr);
					
				    Attr valueAttr = doc.createAttribute("value");
				    valueAttr.setValue("7680");
					amount.setAttributeNode(valueAttr);
			        
					 Element paymentDetails = doc.createElement("paymentDetails");
				     order.appendChild(paymentDetails);
				     
				     Element paymentMethod = doc.createElement("VISA-SSL");
				     paymentDetails.appendChild(paymentMethod);
				     
				     Element cardNumber = doc.createElement("cardNumber");
				     cardNumber.appendChild(doc.createTextNode("4111111111111111"));
				     paymentMethod.appendChild(cardNumber);
				     
				     Element expiryDate = doc.createElement("expiryDate");
				     paymentMethod.appendChild(expiryDate);
				     
				     Element date = doc.createElement("date");
				     expiryDate.appendChild(date);
				     
				     Attr monthAttr = doc.createAttribute("month");
				     monthAttr.setValue("07");
				     date.setAttributeNode(monthAttr);
				     
				     Attr yearAttr = doc.createAttribute("year");
				     yearAttr.setValue("2025");
				     date.setAttributeNode(yearAttr);
				     
				     Element cardHolderName = doc.createElement("cardHolderName");
				     cardHolderName.appendChild(doc.createTextNode("Test"));
				     paymentMethod.appendChild(cardHolderName);
				     
				     Element cvc = doc.createElement("cvc");
				     cvc.appendChild(doc.createTextNode("101"));
				     paymentMethod.appendChild(cvc);
				     
				     Element cardAddress = doc.createElement("cardAddress");
				     paymentMethod.appendChild(cardAddress);
				     
				     Element address = doc.createElement("address");
				     cardAddress.appendChild(address);
				     
				     Element address1 = doc.createElement("address1");
				     address1.appendChild(doc.createTextNode("Test"));
				     address.appendChild(address1);
				     
				     Element address2 = doc.createElement("address2");
				     address2.appendChild(doc.createTextNode(""));
				     address.appendChild(address2);
				     
				     Element address3 = doc.createElement("address3");
				     address3.appendChild(doc.createTextNode(""));
				     address.appendChild(address3);
				     
				     Element postalCode = doc.createElement("postalCode");
				     postalCode.appendChild(doc.createTextNode("SW1A1AA"));
				     address.appendChild(postalCode);
				     
				     Element city = doc.createElement("city");
				     city.appendChild(doc.createTextNode("london"));
				     address.appendChild(city);
				     
				     Element state = doc.createElement("state");
				     state.appendChild(doc.createTextNode("NA"));
				     address.appendChild(state);
				     
				     Element countryCode = doc.createElement("countryCode");
				     countryCode.appendChild(doc.createTextNode("GB"));
				     address.appendChild(countryCode);
				     
				     Element telephoneNumber = doc.createElement("telephoneNumber");
				     telephoneNumber.appendChild(doc.createTextNode("1234567980"));
				     address.appendChild(telephoneNumber);
				     
				     Element session = doc.createElement("session");
				     paymentDetails.appendChild(session);
				     
				     Attr shopperIPAddress = doc.createAttribute("shopperIPAddress");
				     shopperIPAddress.setValue("61.12.91.138");
				     session.setAttributeNode(shopperIPAddress);
				     
				     Attr idAttr = doc.createAttribute("id");
				     idAttr.setValue("6383A29AE40E5839DCA4D17EE86A2495.adm-mymms-rev2v162-int001");
				     session.setAttributeNode(idAttr);
				     
				     Element shopper = doc.createElement("shopper");
				     order.appendChild(shopper);
				     
				     Element shopperEmailAddress = doc.createElement("shopperEmailAddress");
				     shopperEmailAddress.appendChild(doc.createTextNode("test@test.com"));
				     shopper.appendChild(shopperEmailAddress);
				     
				     Element browser = doc.createElement("browser");
				     shopper.appendChild(browser);
				     
				     Element acceptHeader = doc.createElement("acceptHeader");
				     acceptHeader.appendChild(doc.createTextNode(WorldpayConstants.ACCEPT_HEADER));
				     browser.appendChild(acceptHeader);
				     
				     Element userAgentHeader = doc.createElement("userAgentHeader");
				     userAgentHeader.appendChild(doc.createTextNode("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36"));
				     browser.appendChild(userAgentHeader);
				     
				     Element statementNarrative = doc.createElement("statementNarrative");
				     statementNarrative.appendChild(doc.createTextNode(WorldpayConstants.SITECODE_US));
				     order.appendChild(statementNarrative);
				     
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
			        
					try {
						  Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				        DOMImplementation domImpl = doc.getImplementation();
				        DocumentType doctype = domImpl.createDocumentType(WorldpayConstants.QUALIFIED_NAME,
				        		WorldpayConstants.PUBLICID,WorldpayConstants.SYSTEMID);
				        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
				        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
				         try {
				        	  StringWriter writer = new StringWriter();
						        transformer.transform(new DOMSource(doc), new StreamResult(writer));
						         output = writer.getBuffer().toString();
						} catch (TransformerException e) {
							e.printStackTrace();
							logger.log(Level.SEVERE, "==TransformerException in createWorldpayRequestWithDummyData==", e.getMessage());
						}
					} catch (TransformerConfigurationException e) {
						e.printStackTrace();
						logger.log(Level.SEVERE, "==TransformerConfigurationException in createWorldpayRequestWithDummyData==", e.getMessage());
					}
			         
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "==ParserConfigurationException in createWorldpayRequestWithDummyData==", e.getMessage());
				}
    	        
		return output;
	}
    
    /**
     * @param rawXML
     * @return
     * Parse the worldpay authorization call reponse xml
     */
    public Map<String, Object> getCardAuthResponse(String rawXML) {

		try {
			Map<String, Object> response = new HashMap<String, Object>();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			WPCardAuthResponseSaxhandler responseSaxhandler = new WPCardAuthResponseSaxhandler();
			parser.parse(new InputSource(new StringReader(rawXML)), responseSaxhandler);
			String authResponse = responseSaxhandler.getResponse();
			String orderCode = responseSaxhandler.getOrderCode();
			String paymentMethod = responseSaxhandler.getPaymentMethod();
	
			response.put(WorldpayConstants.AUTH_RESPONSE, authResponse);
			response.put(WorldpayConstants.ORDER_CODE, orderCode);
			response.put(WorldpayConstants.PAYMENT_METHOD, paymentMethod);
			return response;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "==ParserConfigurationException in createXmlRequest==", ex.getMessage());
		}

		return null;
	}
}

