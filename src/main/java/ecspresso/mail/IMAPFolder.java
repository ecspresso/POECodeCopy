package ecspresso.mail;

import ecspresso.input.IStoppableThread;
import ecspresso.input.InputListener;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.FromTerm;
import jakarta.mail.search.SearchTerm;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Properties;

public class IMAPFolder implements IStoppableThread {
    private final Properties properties;
    private String username;
    private String password;
    private com.sun.mail.imap.IMAPFolder folderToParse;
    private Store store;
    private Thread idleThread;

    IMAPFolder() {
        properties = System.getProperties();
        properties.setProperty("mail.imaps.connectiontimeout", "30000");
        properties.setProperty("mail.imap.connectiontimeout", "30000");
        properties.setProperty("mail.store.protocol", "imap");
        properties.put("folder_to_parse", "Inbox");
    }

    public void stopRunning() {
        idleThread.interrupt();

        try {
            folderToParse.close();
            store.close();
        } catch(MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void collectEmails(String fromAddress, IMessageHandler messageHandler) throws MessagingException {
        String folderToParseName = properties.getProperty("folder_to_parse");

        // Koppla upp mot mejl servern.
        Session session = Session.getInstance(properties);
        store = session.getStore();
        store.connect(username, password);


        // Hämta och öppna katalogen.
        folderToParse = (com.sun.mail.imap.IMAPFolder) store.getFolder(folderToParseName);
        folderToParse.open(Folder.READ_WRITE);

        folderToParse.addMessageCountListener(new MessageCountAdapter() {
            public void messagesAdded(MessageCountEvent event) {
                Message[] newMessages = event.getMessages();
                try {
                    messageHandler.handle(newMessages);
                } catch(MessagingException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Hämta alla mejl i katalogen.
        SearchTerm searchTerm = new FromTerm(new InternetAddress(fromAddress));
        try {
            Message[] messages = folderToParse.search(searchTerm);
            if(messages.length > 0) {
                messageHandler.handle(folderToParse.search(searchTerm));
            } else {
                System.out.println("Did not find any emails from support@grindinggear.com, waiting for new emails to arrive.");
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        idleThread = new Thread(() -> {
            while(messageHandler.latestMessageAge().isBefore(LocalDateTime.now().minusMinutes(3))) {
                try {
                    folderToParse.idle();
                } catch(MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    void setUsername(String username) {
        this.username = username;
        properties.setProperty("mail.imaps.user", username);
        properties.setProperty("mail.imap.user", username);
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setHostName(String serverIn) {
        properties.setProperty("mail.imaps.host", serverIn);
        properties.setProperty("mail.imap.host", serverIn);
    }

    void setPort(String portIn) {
        properties.setProperty("mail.imaps.port", portIn);
        properties.setProperty("mail.imap.port", portIn);
    }

    public void enableTLS() {
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.ssl.enable", "true");
    }

    public void setFolderToParse(String folderToParse) {
        properties.put("folder_to_parse", folderToParse);
    }
}
