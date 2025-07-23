package com.example.RealTimeChatApplication.repositories;

import com.example.RealTimeChatApplication.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    Optional<User> findByEmailId(String emailId);

    Optional<User> findById(int userId);

    @Query("SELECT u FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "AND u.id NOT IN (SELECT c.contactPerson.id FROM Contact c WHERE c.owner.id = :ownerId AND c.type = 'USER')")
    List<User> findUsersNotInContacts(@Param("searchTerm") String searchTerm,
                                      @Param("ownerId") Integer ownerId);

    boolean existsByEmailId(String email);
}
