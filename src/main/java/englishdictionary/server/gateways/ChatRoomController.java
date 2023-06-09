package englishdictionary.server.gateways;

import englishdictionary.server.dtos.MessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatRoomController {
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageDTO sendMessage(MessageDTO message) {
        return message;
    }
}
