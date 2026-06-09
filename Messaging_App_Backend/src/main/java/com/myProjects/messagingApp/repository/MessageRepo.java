package com.myProjects.messagingApp.repository;

import com.myProjects.messagingApp.dto.MessageDto;
import com.myProjects.messagingApp.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<MessageEntity, Long> {

     String query = """
             SELECT content, timestamp, receiver_id, sender_id FROM messages
             	WHERE (sender_id = :contactId OR sender_id = :currentUserId)\s
             	AND\s
             	(receiver_id = :contactId OR receiver_id = :currentUserId);
           \s""";

    @Query(value=query, nativeQuery = true)
    List<MessageDto> getMessages(@Param("currentUserId") Long currentUserId, @Param("contactId") Long contactId);
}
