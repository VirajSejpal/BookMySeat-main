package il.cshaifasweng.OCSFMediatorExample.server.ocsf;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String toEmail, String subject, String body) {
        // Email configurations
        String host = "smtp.gmail.com";
        String port = "587";
        final String username = "bookmyseatofficial@gmail.com"; // your Gmail username
        final String appPassword = "imzl zdnq lchf cmfa"; // your newly generated app-specific password

        // Set up properties for the SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS

        // Create a session with an authenticator
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(username));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(body);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (AuthenticationFailedException e) {
            System.out.println(ANSI_RED + "Email sending failed: Authentication denied for " + username
                    + ". Please check your App Password in EmailSender.java" + ANSI_RESET);
        } catch (MessagingException e) {
            System.err.println("Email sending failed due to a messaging exception: " + e.getMessage());
        }
    }

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
}
