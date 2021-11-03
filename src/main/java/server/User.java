package server;

import java.io.PrintWriter;
import java.util.Objects;

public class User {

    private String userName;

    private final PrintWriter out;

    public User(String userName, PrintWriter out) {
        this.userName = userName;
        this.out = out;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
