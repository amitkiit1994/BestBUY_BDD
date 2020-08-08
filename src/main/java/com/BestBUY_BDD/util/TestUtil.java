package com.BestBUY_BDD.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import com.google.common.io.Files;
import javax.mail.*;
import javax.mail.internet.*;

import org.jsoup.Jsoup;

import javax.activation.*;
import com.BestBUY_BDD.base.TestBase;
import com.google.common.base.Charsets;

public class TestUtil extends TestBase{
	
	public static void sendMail() throws IOException {
		Properties props=new Properties();
	    props.put("mail.smtp.auth", true);
	    props.put("mail.smtp.starttls.enable", true);
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");


	    Session session = Session.getInstance(props,
	            new javax.mail.Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(prop.getProperty("From_Mail_Recipient"), prop.getProperty("From_Mail_Recipient_Password"));
	                }
	            });
		try {
			
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(prop.getProperty("From_Mail_Recipient")));
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(prop.getProperty("To_Mail_Recipient")));

	         // Set Subject: header field
	         message.setSubject("Best Buy Test Results");

	         BodyPart messageBodyPart = new MimeBodyPart();
	         BodyPart messageBodyPart1 = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setText("TEST RESULTS by Amit");

	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
//	         messageBodyPart = new MimeBodyPart();
//	         String filename = System.getProperty("user.dir")
//						+ "\\target\\surefire-reports\\index.html";
//	         DataSource source = new FileDataSource(filename);
//	         messageBodyPart.setDataHandler(new DataHandler(source));
//	         messageBodyPart.setFileName(filename);
//	         multipart.addBodyPart(messageBodyPart);
	         messageBodyPart1 = new MimeBodyPart();
	         String filename1 = System.getProperty("user.dir")
						+ "\\target\\surefire-reports\\emailable-report.html";
	         String content = Jsoup.parse(new File(filename1), "UTF-8").toString();
	         messageBodyPart1.setContent(content,"text/html");
	         
	         multipart.addBodyPart(messageBodyPart1);
	         // Send the complete message parts
	         message.setContent(multipart);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
		
	}
	
}
