package ca.mcgill.ecse321.repairsystem.dto;

import java.time.LocalDateTime;

public class ChatMessageDto {
    private int id;
    private String content;
    private LocalDateTime timestamp;
    private String senderEmail;
    private int senderId;
    private int receiverId;
    private boolean seen;

    public ChatMessageDto() {
    }

    public ChatMessageDto(int id, String content, LocalDateTime timestamp, String senderEmail, int senderId,
            int receiverId,
            boolean seen) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.senderEmail = senderEmail;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.seen = seen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
