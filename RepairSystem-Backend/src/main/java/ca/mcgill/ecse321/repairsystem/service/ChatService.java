package ca.mcgill.ecse321.repairsystem.service;

import ca.mcgill.ecse321.repairsystem.dao.ChatMessageRepository;
import ca.mcgill.ecse321.repairsystem.dao.PersonRepository;
import ca.mcgill.ecse321.repairsystem.model.ChatMessage;
import ca.mcgill.ecse321.repairsystem.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public ChatMessage createMessage(int senderId, int receiverId, String content) {
        Person sender = personRepository.findById(senderId).orElse(null);
        Person receiver = personRepository.findById(receiverId).orElse(null);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Sender or Receiver not found");
        }

        ChatMessage message = new ChatMessage(content, sender, receiver, null);
        return chatMessageRepository.save(message);
    }

    @Transactional
    public List<ChatMessage> getMessagesBetweenUsers(int user1Id, int user2Id) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(user1Id,
                user2Id, user2Id, user1Id);
    }

    @Transactional
    public void markMessagesAsSeen(int senderId, int receiverId) {
        List<ChatMessage> messages = getMessagesBetweenUsers(senderId, receiverId);
        for (ChatMessage message : messages) {
            if (message.getReceiver().getId() == receiverId && !message.isSeen()) {
                message.setSeen(true);
                chatMessageRepository.save(message);
            }
        }
    }
}
