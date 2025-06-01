package com.example.RealTimeChatApplication.model.message;

import com.example.RealTimeChatApplication.model.files.SharedFile;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.model.group.Group;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ind_recipient_id")
    private User indRecipient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grp_recipient_id")
    private Group grpRecipient;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @OneToMany(mappedBy = "linkedMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SharedFile> sharedFiles = new HashSet<>();

    private boolean isContainsFile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + (sender != null ? sender.getId() : "null") +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", indRecipientId=" + (indRecipient != null ? indRecipient.getId() : "null") +
                ", grpRecipientId=" + (grpRecipient != null ? grpRecipient.getId() : "null") +
                '}';
    }
}
