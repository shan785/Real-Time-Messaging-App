package com.myProjects.messagingApp.repository;

import com.myProjects.messagingApp.dto.ContactDto;
import com.myProjects.messagingApp.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<ContactEntity, Long> {

    String query = """
            SELECT contact_user_id, contact_user_name FROM contacts
                WHERE owner_user_id = :ownerUserId;
            """;

    @Query(value = query, nativeQuery = true)
    List<ContactDto> findByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

}
