package chat.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Golem765 on 11.05.2016.
 *
 */
class ClientGuiModel
{
    private final Set<String> allUserNames = new HashSet<>();
    private String newMessage;

    void addUser(String newUserName)
    {
        allUserNames.add(newUserName);
    }

    void deleteUser(String userName)
    {
        allUserNames.remove(userName);
    }

    Set<String> getAllUserNames()
    {
        return Collections.unmodifiableSet(allUserNames);
    }

    String getNewMessage()
    {
        return newMessage;
    }

    void setNewMessage(String newMessage)
    {
        this.newMessage = newMessage;
    }
}
