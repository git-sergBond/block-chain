package ss.bond.blockchain.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ss.bond.blockchain.dto.MessageDto;
import ss.bond.blockchain.repository.ChatParticipantsRepository;
import ss.bond.blockchain.service.chat.ConnectionPool;

/**
 * WebSocket with STOMP.
 *
 */
@Controller
public class ChatController {

    Logger log = LoggerFactory.getLogger(ChatController.class.getName());

    @Autowired
    private ChatParticipantsRepository repository;

    @Autowired
    private ConnectionPool connectionPool;

    @MessageMapping("/send")
    public void sendHandler(MessageDto dto, @Header("simpSessionId") String sessionId) {
        log.debug("sendHandler() - start: sessionId={}", sessionId);

        String userName = repository.getUserNameBySessionId(sessionId);
        connectionPool.broadcastNewMessage(userName, dto.getMessage());

        log.debug("sendHandler() - end: sessionId={} userName={}", sessionId, userName);
    }
}
