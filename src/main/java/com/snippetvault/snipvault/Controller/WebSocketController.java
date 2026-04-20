package com.snippetvault.snipvault.Controller;

import com.snippetvault.snipvault.DTO.SnippetActivityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    // Client sends to /app/activity
    // Server broadcasts to /topic/activity
    @MessageMapping("/activity")
    @SendTo("/topic/activity")
    public SnippetActivityEvent broadcastActivity(SnippetActivityEvent event) {
        return event; // whatever comes in, broadcast it out
    }
}
// Client → /app/activity → Server logic → /topic/activity → Clients
/*
@MessageMapping("/activity") — listens for messages sent to /app/activity (remember /app prefix from WebSocketConfig)
@SendTo("/topic/activity") — whatever this method returns gets broadcast to ALL subscribers of /topic/activity
@Controller not @RestController — WebSocket methods don't return HTTP responses, so we don't need @ResponseBody
 */