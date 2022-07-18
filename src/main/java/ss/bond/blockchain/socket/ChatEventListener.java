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

@Service
public class ChatEventListener {

    Logger log = LoggerFactory.getLogger(ChatEventListener.class.getName());

    @Autowired
    private ChatParticipantsRepository repository;

    @Autowired
    private ConnectionPool connectionPool;

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String userName = getUserName(headers);
        String sessionId = headers.getSessionId();

        repository.connectUser(sessionId, userName);

        int countUsers = repository.getCountUsers();

        connectionPool.sendWelcomeMessage(sessionId, userName, countUsers);
        //connectionPool.broadcastUserJoin(userName);

        log.debug("*** CONNECTED *** {}", repository.getConnectedUserNames());
    }

    @EventListener
    public void handleSessionConnectEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        repository.disconnectUser(headers.getSessionId());

        System.out.println(repository.getConnectedUserNames());

        String userName = getUserName(headers);
        connectionPool.broadcastUserQuit(userName);

        log.debug("*** DISCONNECTED *** {}", userName);
    }

    private String getUserName(SimpMessageHeaderAccessor headers) {
        return (String) ((ArrayList) (((Map) headers.getHeader("nativeHeaders")).get("user_name"))).get(0);
    }
}