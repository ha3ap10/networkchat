package settings;

public enum Constants {

    CONFIRM("/confirm"),
    CONTACTS_LIST("/contacts"),
    EXIT("/exit"),
    FAIL_ADD_USER("/failAddUser "),
    NEW_USER("/startClient"),
    STOP_SERVER("/stop");

    private String title;

    Constants(String title) {
        this.title = title;
    }

    public String get() {
        return title;
    }
}
