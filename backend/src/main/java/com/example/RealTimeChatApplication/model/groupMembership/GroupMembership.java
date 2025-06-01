package com.example.RealTimeChatApplication.model.groupMembership;

import com.example.RealTimeChatApplication.model.group.GroupRole;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.model.group.Group;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "groupMemberId")
    private User groupMemberId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "groupId")
    private Group groupId;

    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMembership that = (GroupMembership) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GroupMembership{" +
                "id=" + id +
                ", groupMemberId=" + (groupMemberId != null ? groupMemberId.getId() : "null") +
                ", groupId=" + (groupId != null ? groupId.getId() : "null") +
                ", joinedAt=" + joinedAt +
                ", role=" + role +
                '}';
    }
}
