package ca.mcgill.ecse321.repairsystem.dao;

import ca.mcgill.ecse321.repairsystem.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByAppointmentIdOrderByTimestampAsc(int appointmentId);

    List<ChatMessage> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(int senderId1,
            int receiverId1, int senderId2, int receiverId2);
}
