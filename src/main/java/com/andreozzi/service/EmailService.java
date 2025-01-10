package com.andreozzi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

     @Autowired
    private JavaMailSender mailSender;


    public void sendEmail(String to, String subject, String content, boolean isHtml) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, isHtml);

        mailSender.send(message);
    }

    public String addedToCartEmail(String nomeCarta){

        String addedtocartEmail = """
            <!DOCTYPE html>
            <html lang="it">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Carta Aggiunta al Carrello</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f8f9fa;
                        color: #333;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        padding: 20px;
                        background-color: #ffffff;
                        border: 1px solid #dddddd;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    h1 {
                        color: #007bff;
                    }
                    p {
                        font-size: 16px;
                        line-height: 1.5;
                    }
                    .cta {
                        margin-top: 20px;
                        text-align: center;
                    }
                    .cta a {
                        display: inline-block;
                        padding: 10px 20px;
                        font-size: 16px;
                        color: #ffffff;
                        background-color: #007bff;
                        text-decoration: none;
                        border-radius: 5px;
                    }
                    .cta a:hover {
                        background-color: #0056b3;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Carta aggiunta al carrello!</h1>
                    <p>Siamo lieti di informarti che la carta desiderata "<strong>%s</strong>" è stata aggiunta al tuo carrello.</p>
                    <p>Puoi procedere al pagamento o inserire un metodo di pagamento valido per automatizzare l'acquisto!</p>
                    <div class="cta">
                        <a href="https://www.cardtrader.com/it/cart/edit" target="_blank">Vai al Carrello</a>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nomeCarta);
        return addedtocartEmail;
    }
        
    
}
