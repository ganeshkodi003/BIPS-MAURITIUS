package com.bornfire.services;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service

public class Passwordsendingmail {

	 public String sendingmailones(String from, String host, String to, String username, String password, String otp) {
	        // Get the session object
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", host);
	        properties.put("mail.smtp.port", "587"); // Port for TLS/STARTTLS
	        properties.put("mail.smtp.auth", "true"); // Enable authentication
	        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
	       // //System.out.println(y);

	        Session session = Session.getInstance(properties, new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, password);
	            }
	        });
	        try {
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));
	            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	            message.setSubject("OTP for verification");

	            // Set OTP as the email content
	            message.setText("Your OTP is: " + otp);

	            // Send the message
	            Transport.send(message);

	            //System.out.println("OTP email sent successfully.");
	            return "OTP email sent successfully.";

	        } catch (MessagingException e) {
	            e.printStackTrace();
	            return "Failed to send OTP email."+otp;
	        }

	 }
}
