package com.quantum_paradigm.socialshare.helper;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.quantum_paradigm.socialshare.dto.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendMail 
{
	@Autowired
	JavaMailSender mailSender;
	
	public void sendVerificationEmail(User userDto) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		String verificationLink = "http://localhost:1122/quantum-socialshare/user/verify?token=" + userDto.getVerificationToken();
		try {
			helper.setFrom("demodem866@gmail.com", "demoBook");
			helper.setTo(userDto.getEmail());
			helper.setSubject("Verify Email");
			 helper.setText("<html><body><h1>Hello " + userDto.getFirstName() + "</h1>"
			            + "<p>Please verify your email by clicking the link below:</p>"
			            + "<a href='" + verificationLink + "'>" + verificationLink + "</a>"
			            + "<h3>Thanks and Regards</h3></body></html>", true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(message);
	}
}
