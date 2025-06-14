package com.email.email.writer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins="*")
public class EmailGeneratorController {
	
	private final EmailGeneratorService emailGeneratorService;
	
	@PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailrequest){
	    System.out.println("Received content: " + emailrequest.getContent());
	    System.out.println("Received tone: " + emailrequest.getTone());

    	String response= emailGeneratorService.generateEmailReply(emailrequest);
		return ResponseEntity.ok(response);
    }
}
