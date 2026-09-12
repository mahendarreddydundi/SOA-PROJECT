package com.resolvenow.complaint;

import jakarta.persistence.*; import java.time.Instant;
@Entity public class Complaint {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title; @Column(nullable=false, length=4000) private String description; @Column(nullable=false) private String customerEmail; @Enumerated(EnumType.STRING) private Status status=Status.OPEN; private Instant createdAt=Instant.now();
 public enum Status { OPEN, IN_PROGRESS, RESOLVED, CLOSED }
 protected Complaint() {} public Complaint(String title,String description,String customerEmail){this.title=title;this.description=description;this.customerEmail=customerEmail;}
 public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public String getCustomerEmail(){return customerEmail;} public Status getStatus(){return status;} public void setStatus(Status status){this.status=status;}
}
