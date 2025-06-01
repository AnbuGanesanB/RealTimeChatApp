package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<Group,Integer> {


    Group findByGroupName(String name);

}
