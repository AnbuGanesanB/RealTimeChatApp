package com.example.RealTimeChatApplication.model.user;

import com.example.RealTimeChatApplication.model.contact.RecipientType;
import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.message.Message;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    @Column(unique = true)
    private String emailId;

    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "contactPerson", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<Contact> contactOf = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "indRecipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> receivedMessages = new HashSet<>();

    @OneToMany(mappedBy = "groupMemberId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMembership> groupMemberships = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType type;

    private String dpPath;

    @Transient
    public boolean isDpAvailable() {
        return dpPath != null && !dpPath.isBlank();
    }

    private String aboutMe;

    @Enumerated(EnumType.STRING)
    private OnlineStatus onlineStatus;

    @Transient
    public String getInitials() {
        String[] parts = userName.trim().split("\\s+");
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
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
