package ecspresso.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDateTime;

public interface IMessageHandler {
    void handle(Message[] messages) throws MessagingException, IOException;
    LocalDateTime latestMessageAge();
    void getCode();
}
