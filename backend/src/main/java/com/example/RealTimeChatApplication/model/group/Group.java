package com.example.RealTimeChatApplication.model.group;

import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.contact.Contact;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true)
    private String groupName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType type;

    @OneToMany(mappedBy = "grpRecipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> receivedMessages = new HashSet<>();

    @OneToMany(mappedBy = "groupId", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<GroupMembership> members = new HashSet<>();

    @OneToMany(mappedBy = "contactGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contact> contactOf = new HashSet<>();

    private String dpPath;

    @Transient
    public boolean isDpAvailable() {
        return dpPath != null && !dpPath.isBlank();
    }

    @Transient
    public String getInitials() {
        String[] parts = groupName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        if (parts.length >= 2) {
            initials.append(parts[0].charAt(0)).append(parts[1].charAt(0));
        } else {
            initials.append(parts[0].charAt(0)).append(parts[0].charAt(1));
        }
        return initials.toString().toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", type=" + type +
                '}';
    }
}
