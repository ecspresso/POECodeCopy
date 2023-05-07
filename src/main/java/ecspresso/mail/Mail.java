package ecspresso.mail;

import jakarta.mail.Address;

import java.util.Date;

public record Mail(Address[] from, String subject, Object body, Date sent) { }
