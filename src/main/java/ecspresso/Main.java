package ecspresso;

import ecspresso.mail.IMAPBuilder;
import ecspresso.mail.IMAPFolder;
import ecspresso.mail.Mail;
import ecspresso.mail.PropertiesFile;
import ecspresso.mail.cli.ConfigureIMAP;
import ecspresso.pathofexile.PathOfExile;
import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

// @Command(name = "poecodecopy", mixinStandardHelpOptions = true, version = "POECodeCopy 1.0.0")
public class Main {
    public static void main(String[] args) {
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
            }

        }


        IMAPBuilder imapBuilder = new IMAPBuilder()
            .setUsername(prop.getProperty("username"))
            .setPassword(prop.getProperty("password"))
            .setServerIn(prop.getProperty("server_in"))
            .setPortIn(prop.getProperty("port_in"))
            .setFolderToParse(prop.getProperty("folder_to_parse"));

        if(prop.getProperty("enable_tls").equals("true")) {
            imapBuilder.enableTLS();
        }

        IMAPFolder imapFolder = imapBuilder.build();

        ArrayList<Mail> mails = imapFolder.parse();


        for(Mail mail: mails) {
            // System.out.println(mail.messageNumber() + " " + mail.from()[0]);
            if(mail.from()[0].toString().equals("Path of Exile <support@grindinggear.com>")) {
                if(mail.received().after(PathOfExile.getReceived())) {
                    PathOfExile.setContent((String) mail.body());
                    PathOfExile.setReceived(mail.received());
                }
            }
        }

        String[] lines = PathOfExile.getContent().split("\\r?\\n");
        for(String line: lines) {
            if(line.matches("^\\w{3}-\\w{3}-\\w{4}$")) {
                StringSelection content = new StringSelection(line);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(content, content);
                System.out.println("Your code \"" + line + "\" has been copied to the clipboard.");
                break;
            }
        }
    }
}