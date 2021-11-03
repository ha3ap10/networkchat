package server;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class UsersList {

    private static Set<User> users;

    public UsersList() {
        users = new CopyOnWriteArraySet<>();
    }

    public boolean addUser(User user) {
        return users.add(user);
    }

    public boolean removeUser(User user) {
        return users.remove(user);
    }

    public Set<User> getUsers() {
        return users;
    }

    public boolean getUser(User user) {
        return users.contains(user);
    }


}
