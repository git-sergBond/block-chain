package ss.bond.blockchain.socket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ss.bond.blockchain.dto.MessageDto;

/**
 * WebSocket with STOMP.
 *
 */
@Controller
public class ChatController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public MessageDto greeting(MessageDto dto) {
        return new MessageDto("Hello, " + dto.getMessage());
    }
}
