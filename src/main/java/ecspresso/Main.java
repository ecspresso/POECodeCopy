package ecspresso;

import ecspresso.mail.IMAPBuilder;
import ecspresso.mail.IMAPFolder;
import ecspresso.mail.Mail;
import ecspresso.mail.PropertiesFile;
import ecspresso.mail.cli.ConfigureIMAP;
import ecspresso.pathofexile.PathOfExile;
import jakarta.mail.MessagingException;
import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws MessagingException, IOException {
        ConfigureIMAP configureIMAP = new ConfigureIMAP();
        try {
            new CommandLine(configureIMAP).parseArgs(args);
        } catch(MissingParameterException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Properties prop;
        try {
            prop = configureIMAP.run();
            System.out.println();

            if(configureIMAP.isSaveOnly()) {
                System.exit(0);
            }
        } catch(NullPointerException e) {
            try (InputStream in = new FileInputStream("email.properties")) {
                prop = new Properties();
                prop.load(in);
            } catch (IOException | NullPointerException ee) {
                System.err.println("Could not find file \"email.properties\", creating it now.");
                prop = PropertiesFile.createFile();
                System.out.println();
            }
        }


        IMAPBuilder imapBuilder = new IMAPBuilder()
            .setUsername(prop.getProperty("username"))
            .setPassword(prop.getProperty("password"))
            .setHostName(prop.getProperty("hostname"))
            .setPort(prop.getProperty("port_in"))
            .setFolderToParse(prop.getProperty("folder_to_parse"));

        if(prop.getProperty("enable_tls").equals("true")) {
            imapBuilder.enableTLS();
        }

        IMAPFolder imapFolder = imapBuilder.build();



        for(int i = 0; i < 5 && PathOfExile.noEmail(); i++) {
            ArrayList<Mail> mails = imapFolder.parse();

            for(Mail mail: mails) {
                if(mail.from()[0].toString().equals("Path of Exile <support@grindinggear.com>")) {
                    if(mail.sent().after(PathOfExile.getSent())) {
                        PathOfExile.setContent((String) mail.body());
                        PathOfExile.setSent(mail.sent());
                        PathOfExile.emailFound();
                    }
                }
            }

            if(PathOfExile.noEmail()) {
                try {
                    System.out.printf("No email was found, waiting 5 seconds (%s out of 5 tries).%n", i+1);
                    Thread.sleep(5000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if(PathOfExile.noEmail()) {
            System.err.println("Could not find the Path Of Exile account unlock email after 5 tries.");
        } else {
            String[] lines = PathOfExile.getContent().split("\\r?\\n");
            for(String line: lines) {
                if(line.matches("^\\w{3}-\\w{3}-\\w{4}$")) {
                    StringSelection content = new StringSelection(line);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(content, content);
                    System.out.println("Your code \"" + line + "\" has been copied to the clipboard.");

                    Date date = new Date();
                    long diffInMillis = System.currentTimeMillis() - date.getTime();
                    // check if the difference is less than 5 minutes (300000 milliseconds)
                    if (diffInMillis < 300000) {
                        System.out.println();
                        System.out.println("This code is 5 minutes (or more) old, it may be an old message.");
                        System.out.println("Run the application if you have a newer email.");
                    }

                    break;
                }
            }
        }

    }
}