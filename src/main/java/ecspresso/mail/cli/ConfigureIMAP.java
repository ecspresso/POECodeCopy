package ecspresso.mail.cli;

import ecspresso.mail.PropertiesFile;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;

import java.util.Properties;


public class ConfigureIMAP {
    @ArgGroup(exclusive = false)
    private Parameters parameters;

    private static class Parameters {
        @Option(names = {"-c", "--configure"}, description = "Configure IMAP settings.", required = true)
        private boolean configure;

        @Option(names = {"-s", "--save-only"}, description = "Only save the new configuration, do not get the POE code.")
        private boolean saveOnly;

        public Properties run() {
            if(configure) return PropertiesFile.createFile();
            else return null;
        }

        public boolean isSaveOnly() {
            return saveOnly;
        }
    }

    public Properties run() {
        return parameters.run();
    }

    public boolean isSaveOnly() {
        return parameters.isSaveOnly();
    }
}
