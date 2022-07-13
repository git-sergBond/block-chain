package ss.bond.blockchain.service.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ss.bond.blockchain.repository.ChatParticipantsRepository;

@Service
public class ConnectionPool {

    @Autowired
    private ChatParticipantsRepository repository;

    public void sendWelcomeMessage() {
        //Sends a welcome message to a newly connected client
    }

    public void broadcast(String  message) {
        //Broadcasts a general message to the en tire pool
    }

    public void broadcastUserJoin() {
        //Calls the broadcast method with a "user join ing" message
    }

    public void broadcastUserQuit() {
        //Calls the broadcast method with a "user quitting" message
    }

    public void broadcastNewMessage() {
        //Calls the broadcast method with a user’ s chat message
    }

    public void listUsers() {
        //Lists all the users in the pool
    }

    public void addNewUserToPool() {
        //Adds a new user to our existing pool
    }

    public void removeUserFromPool() {
        //Removes an existing user from our pool
    }
}
