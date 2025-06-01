package com.example.RealTimeChatApplication.model.contact;

import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")   // Tracks the **owner** of the contact
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ind_contact_id")   // Tracks the **contacted person**
    private User contactPerson;

    private String nickName;

    @Column
    private LocalDate addedDate;

    private LocalDateTime lastVisitedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grp_contact_id")   // Tracks the **contacted group**
    private Group contactGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType type;

    private int unreadMessages;

    @ManyToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", ownerId=" + (owner != null ? owner.getId() : "null") +
                ", contactPersonId=" + (contactPerson != null ? contactPerson.getId() : "null") +
                ", contactGroupId=" + (contactGroup != null ? contactGroup.getId() : "null") +
                ", nickName='" + nickName + '\'' +
                ", addedDate=" + addedDate +
                ", lastVisitedAt=" + lastVisitedAt +
                ", type=" + type +
                ", unreadMessages=" + unreadMessages +
                '}';
    }
}
