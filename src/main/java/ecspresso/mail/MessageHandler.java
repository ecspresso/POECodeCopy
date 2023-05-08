package ecspresso.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class MessageHandler implements IMessageHandler {
    private String content;
    private LocalDateTime sent = LocalDateTime.MIN;

    @Override
    public synchronized void handle(Message[] messages) throws MessagingException, IOException {
        for(Message message: messages) {
            LocalDateTime age = getAge(message.getSentDate());
            if(age.isAfter(sent)) {
                content = (String) message.getContent();
                sent = age;
            }
        }

        getCode();
    }

    @Override
    public LocalDateTime latestMessageAge() {
        return sent;
    }

    @Override
    public void getCode() {
        boolean notFound = true;
        String[] lines = content.split("\\r?\\n");

        for(String line: lines) {
            if(line.matches("^\\w{3}-\\w{3}-\\w{4}$")) {
                StringSelection content = new StringSelection(line);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(content, content);
                System.out.println("Your code \"" + line + "\" has been copied to the clipboard.");

                if(sent.isBefore(LocalDateTime.now().minusMinutes(3))) {
                    System.out.println("This code is at least 3 minute old, the program will wait for new messages.");
                }
                notFound = false;
            }
        }

        if(notFound) {
            System.out.println("Did not find the right email from support@grindinggear.com.");
        }

        System.out.println("The application will wait for new message.");
    }

    private LocalDateTime getAge(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
