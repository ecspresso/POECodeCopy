package ecspresso;

import ecspresso.input.InputListener;
import ecspresso.mail.IMAPBuilder;
import ecspresso.mail.IMAPFolder;
import ecspresso.mail.MessageHandler;
import ecspresso.mail.PropertiesFile;
import ecspresso.mail.cli.ConfigureIMAP;
import jakarta.mail.MessagingException;
import picocli.CommandLine;
import picocli.CommandLine.MissingParameterException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

        MessageHandler messageControl = new MessageHandler();

        IMAPFolder imapFolder = imapBuilder.build();
        imapFolder.collectEmails("support@grindinggear.com", messageControl);

        InputListener inputListener = new InputListener(imapFolder);
        inputListener.start();
    }
}