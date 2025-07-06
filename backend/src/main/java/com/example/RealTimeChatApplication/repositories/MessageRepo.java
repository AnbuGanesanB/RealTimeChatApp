package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.message.Message;
import com.example.RealTimeChatApplication.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface MessageRepo extends JpaRepository<Message,Integer> {

    List<Message> findBySenderAndIndRecipient(User sender, User indRecipient);

    List<Message> findBySenderAndGrpRecipient(User sender, Group grpRecipient);

    List<Message> findByGrpRecipient(Group grpRecipient);

    List<Message> findBySenderAndGrpRecipientAndTimestampBefore(User sender, Group grpRecipient, LocalDateTime timestamp);

    List<Message> findByGrpRecipientAndTimestampBefore(Group grpRecipient, LocalDateTime timestamp);

}
