package ca.mcgill.ecse321.repairsystem.dto;

public class ChatMessageRequest {
    private String content;
    private int senderId;
    private int receiverId;

    public ChatMessageRequest() {
    }

    public ChatMessageRequest(String content, int senderId, int receiverId) {
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
