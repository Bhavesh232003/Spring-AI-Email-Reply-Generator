package com.email.email.writer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {
	
	private final WebClient webclient;
	
	@Value("${gemini.api.url}")
	private String geminiApiUrl;

	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	public EmailGeneratorService(WebClient.Builder webclient) {
		this.webclient = webclient.build();
	}
	
	public String generateEmailReply(EmailRequest emailRequest) {
		 //Build the prompt
		String prompt=buildPrompt(emailRequest);
		//Craft a request
		Map<String,Object> requestBody=Map.of(
				"contents",new Object[] {
						Map.of("parts", new Object[] {
								Map.of("text",prompt)
						})
				}
		);
		// Do request and get a response 
		String response=webclient.post()
				.uri(geminiApiUrl + geminiApiKey)
				.header("Content-Type","application/json")
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		
		//return reponse
		return extractResponseContent(response);
	}

	private String extractResponseContent(String response) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper mapper=new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			return rootNode.path("candidates")
					.get(0).path("content")
					.path("parts").get(0)
					.path("text").asText();
		}
		catch(Exception e) {
			return "Error Processing Request "+e.getMessage();
		}
	}

	private String buildPrompt(EmailRequest emailRequest) {
		// TODO Auto-generated method stub
		StringBuilder prompt=new StringBuilder();
		prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line.");
		if(emailRequest.getTone() !=null && !emailRequest.getTone().isEmpty()) {
			prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
		}
		prompt.append("\n Original email: \n").append(emailRequest.getContent());
		return prompt.toString();
	}

}
