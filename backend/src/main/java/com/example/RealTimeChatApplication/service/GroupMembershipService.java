package com.example.RealTimeChatApplication.service;

import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import com.example.RealTimeChatApplication.model.groupMembership.MembershipStatus;
import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.repositories.GroupMembershipRepo;
import com.example.RealTimeChatApplication.repositories.GroupRepo;
import com.example.RealTimeChatApplication.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GroupMembershipService {

    private final GroupMembershipRepo groupMembershipRepo;
    private final UserRepo userRepo;
    private final GroupRepo groupRepo;
    private final EntityManager entityManager;

    @Transactional
    public GroupMembership createNewGroupMembership(User newMember, Group group){
        GroupMembership groupMembership = new GroupMembership();

        groupMembership.setJoinedAt(LocalDateTime.now());
        groupMembership.setMembershipStatus(MembershipStatus.ACTIVE);

        groupMembership.setGroupId(group);
        group.getMembers().add(groupMembership);

        groupMembership.setGroupMemberId(newMember);
        newMember.getGroupMemberships().add(groupMembership);

        groupMembership = groupMembershipRepo.save(groupMembership);
        userRepo.save(newMember);
        groupRepo.save(group);
        return groupMembership;
    }

    public GroupMembership findGroupMembership(Group group, User user){
        return groupMembershipRepo.findByGroupIdAndGroupMemberId(group,user)
                .orElseThrow(() -> new NoSuchElementException("No MemberShip found"));
    }
}
