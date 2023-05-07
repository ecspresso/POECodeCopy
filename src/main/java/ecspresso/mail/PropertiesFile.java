package ecspresso.mail;

import ch.qos.logback.core.util.FileUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class PropertiesFile {
    public static Properties createFile() {
        Scanner input = new Scanner(System.in);
        Properties properties = new Properties();

        System.out.printf("%-22s", "Enter IMAP username:");
        properties.put("username", input.nextLine());

        System.out.printf("%-22s", "Enter IMAP password:");
        properties.put("password", input.nextLine());

        System.out.printf("%-22s", "Enter IMAP host name:");
        properties.put("server_in", input.nextLine());

        System.out.printf("%-22s", "Enter IMAP host port:");
        properties.put("port_in", input.nextLine());

        System.out.printf("%-22s", "Enter IMAP folder to:");
        properties.put("folder_to_parse", input.nextLine());

        System.out.printf("%-22s", "Enable TLS? (Y/N)");
        String tls;
        while(!(tls = input.nextLine()).matches("^[Nn]([oO])?|[Yy]([Ee][Ss])?$")) {
            System.out.println("Invalid input, please enter 'y' or 'n'");
            System.out.printf("%-22s", "Enable TLS? (Y/N)");
        }

        if(tls.matches("^[Yy]([Ee][Ss])?$"))
            properties.put("enable_tls", "true");
        else
            properties.put("enable_tls", "false");

        try(final OutputStream outputstream = new FileOutputStream("email.properties")) {
            properties.store(outputstream, "POECodeCopy configuration file");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}
