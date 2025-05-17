package com.example.bookadv.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoContacto(String nombre, String email, String motivo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("jgilsab99@gmail.com"); // Email del gerente
            helper.setSubject("Nuevo mensaje de contacto");
            helper.setText("Nombre: " + nombre + "\nEmail: " + email + "\nMotivo: " + motivo);

            mailSender.send(message);
            System.out.println("¡Correo enviado exitosamente!");

             // Correo para el usuario (confirmación)
            MimeMessage confirmationMessage = mailSender.createMimeMessage();
            MimeMessageHelper confirmationHelper = new MimeMessageHelper(confirmationMessage, true);

            confirmationHelper.setTo(email);  // Correo del usuario
            confirmationHelper.setSubject("Confirmación de contacto");
            confirmationHelper.setText("Gracias por ponerte en contacto con nosotros. Tu mensaje ha sido recibido.");

            mailSender.send(confirmationMessage);
            System.out.println("¡Correo de confirmación enviado exitosamente al usuario!");
    

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
