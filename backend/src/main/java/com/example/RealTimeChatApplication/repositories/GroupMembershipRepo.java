package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.groupMembership.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMembershipRepo extends JpaRepository<GroupMembership, Integer> {

}
