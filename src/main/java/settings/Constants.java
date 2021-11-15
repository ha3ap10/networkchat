package settings;

public enum Constants {

    CLIENT_ENTER_NAME("\nВведите имя:"),
    CLIENT_FAIL_ADD_USER("Не удалось зарегистрировать пользователя"),
    CONFIRM("/confirm"),
    CONFIRM_REG("Registered as "),
    CONNECT("New connection accepted: "),
    CONTACTS_LIST("/contacts"),
    DISCONNECT("Disconnected: "),
    EXIT("/exit"),
    FAIL_ADD_USER("/failAddUser "),
    IS_OFF(" is offline"),
    IS_ON(" is online"),
    NEW_USER("/startClient"),
    READ("Read: "),
    SEND("Send: "),
    SEND_CONTACT_LIST("Send contact list to "),
    SERVER_SHUTDOWN("/serverShutdown"),
    SERVER_SHUTDOWN_MSG("Server shutdown, press 'ENTER' to exit."),
    SERVER_STOP("/stop"),
    STOP_MSG("Stopped"),
    TIME_OUT("Accept timed out");

    private String title;

    Constants(String title) {
        this.title = title;
    }

    public String get() {
        return title;
    }
}
