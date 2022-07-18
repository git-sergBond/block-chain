package ss.bond.blockchain.service.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ss.bond.blockchain.dto.MessageDto;

@Service
public class ConnectionPool {

    private String USER_DESTINATION = "/queue/messages";
    private String BROADCAST_DESTINATION = "/topic/broadcast";

    private Logger log = LoggerFactory.getLogger(ConnectionPool.class.getName());

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    /**
     * Sends a welcome message to a newly connected client.
     */
    public void sendWelcomeMessage(String sessionId, String userName, int countUsers) {
        String message = "Welcome " + userName + ". There are " + (countUsers - 1) + " beside you.";

        try {
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendToUser(sessionId, message);
    }

    /**
     * Calls the broadcast method with a "user joining" message.
     */
    public void broadcastUserJoin(String userName) {
        broadcast("User " + userName + " is joining!");
    }

    /**
     * Calls the broadcast method with a "user quitting" message.
     */
    public void broadcastUserQuit(String userName) {
        broadcast("User " + userName + " is quitting!");
    }

    /**
     * Calls the broadcast method with a user’ s chat message.
     */
    public void broadcastNewMessage(String userName, String message) {
        broadcast("[" + userName + "] > " + message);
    }

    /**
     * Broadcasts a general message to the en tire pool.
     */
    private void broadcast(String  message) {
        log.debug("broadcast() - start: message={}", message);
        brokerMessagingTemplate.convertAndSend(BROADCAST_DESTINATION, new MessageDto(message));
        log.debug("broadcast() - end: message={}", message);
        //TODO Нам не нужно также транслировать на пользователя, отправляющго сообщение (попробовать исключить по SessionId из броадкаста если возможно)
    }

    /**
     * Sends a message to concrete user.
     */
    private void sendToUser(String sessionId, String message) {
        log.debug("sendToUser() - start: sessionId={} message={}", sessionId, message);
        brokerMessagingTemplate.convertAndSendToUser(sessionId, USER_DESTINATION, new MessageDto(message));
        log.debug("sendToUser() - end: sessionId={} message={}", sessionId, message);
    }
}
