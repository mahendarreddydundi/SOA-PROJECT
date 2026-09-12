package com.resolvenow.complaint;
import org.springframework.stereotype.Service; import java.util.*;
@Service public class ComplaintService { private final ComplaintRepository repository; private final AssignmentClient assignmentClient;
 public ComplaintService(ComplaintRepository repository,AssignmentClient assignmentClient){this.repository=repository;this.assignmentClient=assignmentClient;}
 public List<Complaint> all(){return repository.findAll();} public Complaint one(Long id){return repository.findById(id).orElseThrow();}
 public Complaint create(Complaint complaint){Complaint saved=repository.save(complaint); try { assignmentClient.assign(new AssignmentClient.AssignmentRequest(saved.getId(),saved.getCustomerEmail())); } catch (RuntimeException ignored) { } return saved;}
 public Complaint updateStatus(Long id,Complaint.Status status){Complaint complaint=one(id);complaint.setStatus(status);return repository.save(complaint);} public void delete(Long id){repository.deleteById(id);}
}
