package ecspresso.mail;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

public class IMAPFolder {
    private final Properties properties;
    private String username;
    private String password;
    private HashSet<String> parsedEmails = new HashSet<>();

    IMAPFolder() {
        properties = System.getProperties();
        properties.setProperty("mail.imaps.connectiontimeout", "30000");
        properties.setProperty("mail.imap.connectiontimeout", "30000");
        properties.setProperty("mail.store.protocol", "imap");
        properties.put("folder_to_parse", "Inbox");
    }


    public synchronized ArrayList<Mail> parse() throws MessagingException, IOException {
        String folderToParseName = properties.getProperty("folder_to_parse");
        ArrayList<Mail> emailMessages = new ArrayList<>();

        // Koppla upp mot mejl servern.
        Session session = Session.getInstance(properties);
        Store store = session.getStore();
        store.connect(username, password);


        // Hämta och öppna katalogen.
        com.sun.mail.imap.IMAPFolder folderToParse = (com.sun.mail.imap.IMAPFolder) store.getFolder(folderToParseName);
        folderToParse.open(Folder.READ_WRITE);

        // Hämta alla mejl i katalogen.
        Message[] messages = folderToParse.getMessages();
        for(Message msg : messages) {
            String[] msgIdHeaders = msg.getHeader("Message-ID");
            if (msgIdHeaders != null && msgIdHeaders.length > 0) {
                String messageId = msgIdHeaders[0];
                if(!parsedEmails.contains(messageId)) {
                    parsedEmails.add(messageId);
                    emailMessages.add(new Mail(msg.getFrom(), msg.getSubject(), msg.getContent(), msg.getSentDate()));
                }
            }
        }

        folderToParse.close();

        return emailMessages;
    }


    void setUsername(String username) {
        this.username = username;
        properties.setProperty("mail.imaps.user", username);
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setHostName(String serverIn) {
        properties.setProperty("mail.imaps.host", serverIn);
    }

    void setPort(String portIn) {
        properties.setProperty("mail.imaps.port", portIn);
    }

    void setServerOut(String serverOut) {
        properties.put("mail.smtp.host", serverOut);
    }

    void setPortOut(String portOut) {
        properties.put("mail.smtp.port", portOut);
    }

    public void enableTLS() {
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.ssl.enable", "true");
    }

    public void setFolderToParse(String folderToParse) {
        properties.put("folder_to_parse", folderToParse);
    }
}
