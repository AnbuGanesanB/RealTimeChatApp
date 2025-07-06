package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.contact.Contact;
import com.example.RealTimeChatApplication.model.group.Group;
import com.example.RealTimeChatApplication.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepo extends JpaRepository<Contact,Integer> {

    Contact findByOwnerAndContactPerson(User owner, User contactPerson);

    Optional<Contact> findByOwnerAndContactGroup(User owner, Group contactGroup);

    List<Contact> findAllByOwner(User owner);

    boolean existsByOwnerAndContactPerson(User owner, User contactPerson);

    boolean existsByOwnerAndContactGroup(User owner, Group contactGroup);


}
