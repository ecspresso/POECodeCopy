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

    public IMAPBuilder setServerIn(String serverIn) {
        imapFolder.setServerIn(serverIn);
        return this;
    }

    public IMAPBuilder setPortIn(String portIn) {
        imapFolder.setPortIn(portIn);
        return this;
    }

    public IMAPBuilder setServerOut(String serverOut) {
        imapFolder.setServerOut(serverOut);
        return this;
    }

    public IMAPBuilder setPortOut(String portOut) {
        imapFolder.setPortOut(portOut);
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
