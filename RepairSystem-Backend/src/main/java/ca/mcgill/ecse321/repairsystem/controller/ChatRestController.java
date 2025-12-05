package ca.mcgill.ecse321.repairsystem.controller;

import ca.mcgill.ecse321.repairsystem.dto.ChatMessageDto;
import ca.mcgill.ecse321.repairsystem.model.ChatMessage;
import ca.mcgill.ecse321.repairsystem.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatMessageDto sendMessage(@RequestBody ca.mcgill.ecse321.repairsystem.dto.ChatMessageRequest request) {
        ChatMessage message = chatService.createMessage(request.getSenderId(), request.getReceiverId(),
                request.getContent());
        return convertToDto(message);
    }

    @GetMapping
    public List<ChatMessageDto> getMessages(@RequestParam int user1Id, @RequestParam int user2Id) {
        List<ChatMessage> messages = chatService.getMessagesBetweenUsers(user1Id, user2Id);
        List<ChatMessageDto> dtos = new ArrayList<>();
        for (ChatMessage message : messages) {
            dtos.add(convertToDto(message));
        }
        return dtos;
    }

    @PutMapping("/seen")
    public void markAsSeen(@RequestParam int senderId, @RequestParam int receiverId) {
        chatService.markMessagesAsSeen(senderId, receiverId);
    }

    private ChatMessageDto convertToDto(ChatMessage message) {
        return new ChatMessageDto(
                message.getId(),
                message.getContent(),
                message.getTimestamp(),
                message.getSender().getEmail(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.isSeen());
    }
}
