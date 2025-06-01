package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MessageRepo extends JpaRepository<Message,Integer> {

    Set<Message> findBySenderAndIndRecipient(User sender, User indRecipient);

    Set<Message> findBySenderAndGrpRecipient(User sender, Group grpRecipient);

}
