package com.resolvenow.complaint;

import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ComplaintServiceTest {
 @Mock ComplaintRepository repository; @Mock AssignmentClient assignmentClient; @Mock NotificationClient notificationClient; @InjectMocks ComplaintService service;
 @Test void createsComplaintAndRequestsAssignment(){ Complaint input=new Complaint("Login issue","Cannot login","customer@example.com"); when(repository.save(input)).thenReturn(input); Complaint saved=service.create(input); verify(repository).save(input); verify(assignmentClient).assign(any()); assertSame(input,saved); }
 @Test void updatesStatusAndNotifiesCustomer(){ Complaint complaint=new Complaint("Login issue","Cannot login","customer@example.com"); when(repository.findById(1L)).thenReturn(java.util.Optional.of(complaint)); when(repository.save(complaint)).thenReturn(complaint); Complaint saved=service.updateStatus(1L,Complaint.Status.IN_PROGRESS); assertSame(complaint,saved); verify(notificationClient).notify(any()); }
}
