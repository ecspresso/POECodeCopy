package ecspresso.mail;

public class IMAPBuilder {
    private final IMAPFolder imapFolder = new IMAPFolder();

    public IMAPBuilder() {
    }

    public IMAPFolder build() {
        return imapFolder;
    }

    public IMAPBuilder setUsername(String username) {
        imapFolder.setUsername(username);
        return this;
    }

    public IMAPBuilder setPassword(String password) {
        imapFolder.setPassword(password);
        return this;
    }

    public IMAPBuilder setHostName(String serverIn) {
        imapFolder.setHostName(serverIn);
        return this;
    }

    public IMAPBuilder setPort(String portIn) {
        imapFolder.setPort(portIn);
        return this;
    }

    public IMAPBuilder enableTLS() {
        imapFolder.enableTLS();
        return this;
    }

    public IMAPBuilder setFolderToParse(String folderToParse) {
        imapFolder.setFolderToParse(folderToParse);
        return this;
    }
}
