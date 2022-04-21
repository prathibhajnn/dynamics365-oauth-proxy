package group.youwinn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

}
