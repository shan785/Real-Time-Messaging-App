package com.myProjects.messagingApp.repository;

import com.myProjects.messagingApp.dto.ContactDto;
import com.myProjects.messagingApp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    String query = """          
            SELECT contact_id, contactUserName FROM
            	users INNER JOIN (
            		SELECT DISTINCT
            			CASE
            				WHEN sender_id = :id THEN receiver_id
            				WHEN receiver_id = :id THEN sender_id
            			END AS contact_id
            		FROM messages WHERE sender_id = :id OR receiver_id = :id
                ) AS unique_contacts
            			ON users.id = unique_contacts.contact_id;
            """;

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    @Query(value = query, nativeQuery = true)
    List<ContactDto> getContacts(@Param("id") Long id);

}
