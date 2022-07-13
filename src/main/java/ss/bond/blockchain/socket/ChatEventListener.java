package ss.bond.blockchain.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ss.bond.blockchain.repository.ChatParticipantsRepository;
import ss.bond.blockchain.service.chat.ConnectionPool;

import java.util.ArrayList;
import java.util.Map;

/**
 * https://github.com/salmar/spring-websocket-chat/blob/master/src/main/java/com/sergialmar/wschat/event/PresenceEventListener.java
 */
@Service
public class ChatEventListener {

    Logger log = LoggerFactory.getLogger(ChatEventListener.class.getName());

    @Autowired
    private ChatParticipantsRepository repository;

    @Autowired
    private ConnectionPool connectionPool;

    /**
     * https://www.baeldung.com/spring-websockets-send-message-to-user (#1)
     * https://stackoverflow.com/questions/34929578/spring-websocket-sendtosession-send-message-to-specific-session (#2 subscribe)
     * https://github.com/spring-projects/spring-framework/issues/15933
     * https://stackoverflow.com/questions/62456213/spring-websocket-convertandsendtouser-not-working-but-convertandsend-working
     * @param event
     */
    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String userName = getUserName(headers);
        String sessionId = headers.getSessionId();

        repository.connectUser(sessionId, userName);
        log.debug("*** CONNECTED *** {}", repository.getConnectedUserNames());

        int countUsers = repository.getCountUsers();

        connectionPool.sendWelcomeMessage(sessionId, userName, countUsers, headers);
    }

    @EventListener
    public void handleSessionConnectEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        repository.disconnectUser(headers.getSessionId());

        System.out.println("*** DISCONNECTED ***");
        System.out.println(repository.getConnectedUserNames());
    }

    private String getUserName(SimpMessageHeaderAccessor headers) {
        return (String) ((ArrayList) (((Map) headers.getHeader("nativeHeaders")).get("user_name"))).get(0);
    }

}