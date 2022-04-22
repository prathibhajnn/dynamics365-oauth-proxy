package group.youwinn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import group.youwinn.util.CommonUtil;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/yod")
public class ProxyController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);
			
	@Autowired
	CommonUtil commonUtil;
	
	@Value( "${3pl_dynamics_url}" )
	String threepl_dynamics_url;
	
	@Value( "${soap_action_3pl}" )
	String soap_action_3pl;
	
	String token = null;
	
	@PostMapping("proxy")
	public String proxyToOauth(@RequestBody String requestbody){
		
		logger.info("ProxyController proxyToOauth started ");
		HttpResponse<String> response =null;
		try {
			token = (token == null) ? commonUtil.getToken() : token;
			
//			response = Unirest.post(threepl_dynamics_url)
//			  .header("SOAPAction", soap_action_3pl)
//			  .header("Authorization", "Bearer "+token)
//			  .header("Content-Type", "application/json")
//			  .body(requestbody)
//			  .asString();
//			
			Unirest.setTimeouts(0, 0);
			 response = Unirest.post(threepl_dynamics_url)
			  .header("Content-Type", "application/json")
			  .header("Authorization", "Bearer "+token)
			  .body(commonUtil.extractCDATAFromRequest(requestbody))
			  .asString();
			
			if(response.getStatus() == 401) {
				//generate fresh token
				logger.info("Token expired generating fresh token.........");
				token = commonUtil.getToken();
				Unirest.setTimeouts(0, 0);
				 response = Unirest.post(threepl_dynamics_url)
				  .header("Content-Type", "application/json")
				  .header("Authorization", "Bearer "+token)
				  .body(commonUtil.extractCDATAFromRequest(requestbody))
				  .asString();
			}else if(response.getStatus() == 200) {
				logger.info("Got success response from 3pl dynamics::"+response.getStatusText());	
				
			}else {
				logger.info("Response from 3pl dynamics::"+response.getStatus()+"::message:::"+response.getStatusText());
			}
			
		} catch (Exception e) {
			logger.error("Error in calling Nav "+e.getMessage()); 
			
		}
		logger.debug("ProxyController proxyToOauth ended..returning response "+response.getBody());
		logger.info("ProxyController proxyToOauth ended..returning response ");
		return response.getBody();
		
	}

}
