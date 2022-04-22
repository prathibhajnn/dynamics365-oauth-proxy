package group.youwinn.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Component
public class CommonUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	@Value( "${get_token_url}" )
	String gettoken_url;
	
	@Value( "${scope}" )
	String scope;
	
	@Value( "${client_id}" )
	String client_id;
	
	@Value( "${client_secret}" )
	String client_secret;
	
	@Autowired
	ObjectMapper objectMapper;
	
	public String getToken() {
		String token = null;
		try {
			com.mashape.unirest.http.HttpResponse<String> response = Unirest.post(gettoken_url)
			  .header("Content-Type", "application/x-www-form-urlencoded")
			  .field("grant_type", "client_credentials")
			  .field("scope", scope)
			  .field("client_id", client_id)
			  .field("client_secret", client_secret)
			  .asString();
			
			if(response.getStatus() == 200) {
				JsonNode node = objectMapper.readTree(response.getBody());
				token = node.get("access_token").textValue();
				logger.debug("Sucessfully generated token:::::::::"+node.get("access_token"));
			}else {
				logger.error("Error generating token: responsecode:"+response.getStatus() +" ::message :"+response.getStatusText());
				
			}
			
		} catch (Exception e) {
			logger.debug("Error generating token:::"+e.getMessage());	
		}
		
		return token;
	}

	public String extractCDATAFromRequest(String data) {
		
		String xslt ="<xsl:stylesheet version=\"1.0\""
				+ " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"> xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ " <xsl:output method=\"text\"/>"
				+ "<xsl:template match=\"/\">"
				+ "  <xsl:value-of select=\"Envelope\"/>\""
				+ "</xsl:template>"
				+ "</xsl:stylesheet>";
		
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		    Document xmlDocument = builder.parse(new InputSource(new StringReader(data)));
		    
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer(new StreamSource(IOUtils.toInputStream(xslt)));
		    StringWriter outWriter = new StringWriter();
		    StreamResult result = new StreamResult( outWriter );
		    
		    transformer.transform(new DOMSource(xmlDocument), result);
		    
		    StringBuffer sb = outWriter.getBuffer(); 
		    String finalstring = sb.toString();
		    
		    String finalString = "{'xml':"+"'"+finalstring.substring(finalstring.indexOf("<"), finalstring.lastIndexOf(">")+1)+"'"+"}";
		    
		    return finalString;

		}
	    catch (Exception e) {
	    	e.printStackTrace();
		}
		return null;
	  
}
	  
	
	
public static String getCharacterDataFromElement(Element e) {
    Node child = e.getFirstChild();
    if (child instanceof org.w3c.dom.CharacterData) {
      org.w3c.dom.CharacterData cd = (org.w3c.dom.CharacterData) child;
      return cd.getData();
    }
    return "";
  }
	
	
}
