package com.myProjects.messagingApp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name = "contacts")
public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerUserId;

    @Column(nullable = false)
    private Long contactUserId;

    @Column(nullable = false)
    private String contactUserName;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime addedAt;

    public ContactEntity(Long id, Long ownerUserId, Long contactUserId, String contactUserName, LocalDateTime addedAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.contactUserId = contactUserId;
        this.contactUserName = contactUserName;
        this.addedAt = addedAt;
    }

    public ContactEntity(Long ownerUserId, Long contactUserId, String contactUserName) {
        this.ownerUserId = ownerUserId;
        this.contactUserId = contactUserId;
        this.contactUserName = contactUserName;
    }

    public ContactEntity() {
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getContactUserName() {
        return contactUserName;
    }

    public void setContactUserName(String contactUserName) {
        this.contactUserName = contactUserName;
    }


}
