package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMembershipRepo extends JpaRepository<GroupMembership, Integer> {

    GroupMembership findByGroupMemberIdAndGroupId(User user, Group group);

    List<GroupMembership> findByGroupIdAndMembershipStatus(Group group, MembershipStatus status);

    default List<GroupMembership> findActiveMembers(Group group) {
        return findByGroupIdAndMembershipStatus(group, MembershipStatus.ACTIVE);
    }

    default List<GroupMembership> findInactiveMembers(Group group) {
        return findByGroupIdAndMembershipStatus(group, MembershipStatus.INACTIVE);
    }

    Optional<GroupMembership> findByGroupIdAndGroupMemberId(Group group, User user);
}
