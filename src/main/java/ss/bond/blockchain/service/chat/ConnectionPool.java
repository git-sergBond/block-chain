package ss.bond.blockchain.service.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ss.bond.blockchain.dto.MessageDto;

@Service
public class ConnectionPool {

    Logger log = LoggerFactory.getLogger(ConnectionPool.class.getName());

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    /**
     * Sends a welcome message to a newly connected client.
     */
    @Async
    public void sendWelcomeMessage(String sessionId, String nickName, int countUsers, SimpMessageHeaderAccessor headers) {
        String message = "Welcome " + nickName + ". There are " + (countUsers - 1) + " beside you.";

        log.debug("sendWelcomeMessage() - sessionId={} {}", sessionId, headers.getMessageHeaders());

        try {
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        brokerMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/messages",
                new MessageDto(message)
        );

        log.debug("sendWelcomeMessage() - {}", message);
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
