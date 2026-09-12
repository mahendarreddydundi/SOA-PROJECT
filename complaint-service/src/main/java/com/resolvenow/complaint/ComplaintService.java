package com.resolvenow.complaint;
import org.springframework.stereotype.Service; import java.util.*;
@Service public class ComplaintService { private final ComplaintRepository repository; private final AssignmentClient assignmentClient; private final NotificationClient notificationClient;
 public ComplaintService(ComplaintRepository repository,AssignmentClient assignmentClient,NotificationClient notificationClient){this.repository=repository;this.assignmentClient=assignmentClient;this.notificationClient=notificationClient;}
 public List<Complaint> all(){return repository.findAll();} public Complaint one(Long id){return repository.findById(id).orElseThrow();}
 public Complaint create(Complaint complaint){Complaint saved=repository.save(complaint); try { assignmentClient.assign(new AssignmentClient.AssignmentRequest(saved.getId(),saved.getCustomerEmail())); } catch (RuntimeException ignored) { } return saved;}
 public Complaint updateStatus(Long id,Complaint.Status status){Complaint complaint=one(id);complaint.setStatus(status);Complaint saved=repository.save(complaint); try { notificationClient.notify(new NotificationClient.NotificationRequest(saved.getId(),saved.getCustomerEmail(),"Complaint status changed to "+saved.getStatus())); } catch (RuntimeException ignored) { } return saved;} public void delete(Long id){repository.deleteById(id);}
}
