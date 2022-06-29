package ss.bond.blockchain.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ss.bond.blockchain.repository.ChatParticipantsRepository;

import java.util.ArrayList;
import java.util.Map;

/**
 * https://github.com/salmar/spring-websocket-chat/blob/master/src/main/java/com/sergialmar/wschat/event/PresenceEventListener.java
 */
@Service
public class ChatEventListener {

    @Autowired
    private ChatParticipantsRepository repository;

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String userName = (String) ((ArrayList)(((Map) headers.getHeader("nativeHeaders")).get("user_name"))).get(0);
        repository.connectUser(headers.getSessionId(), userName);

        System.out.println("*** CONNECTED ***");
        System.out.println(repository.getConnectedUserNames());
    }

    @EventListener
    public void handleSessionConnectEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        repository.disconnectUser(headers.getSessionId());

        System.out.println("*** DISCONNECTED ***");
        System.out.println(repository.getConnectedUserNames());
    }

}