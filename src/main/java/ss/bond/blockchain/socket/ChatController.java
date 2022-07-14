package ss.bond.blockchain.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import ss.bond.blockchain.dto.MessageDto;

/**
 * WebSocket with STOMP.
 *
 */
@Controller
public class ChatController {

    Logger log = LoggerFactory.getLogger(ChatController.class.getName());

    @MessageMapping("/hello")
    @SendToUser("/queue/reply")
    public MessageDto greeting(MessageDto dto, @Header("simpSessionId") String sessionId) {
        log.debug("greeting(): sessionId={}", sessionId);
        return new MessageDto("Hello, " + dto.getMessage());
    }
}
