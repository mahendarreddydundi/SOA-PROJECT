package com.resolvenow.complaint;

import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ComplaintServiceTest {
 @Mock ComplaintRepository repository; @Mock AssignmentClient assignmentClient; @Mock NotificationClient notificationClient; @InjectMocks ComplaintService service;
 @Test void createsComplaintAndRequestsAssignment(){ Complaint input=new Complaint("Login issue","Cannot login","customer@example.com"); when(repository.save(input)).thenReturn(input); Complaint saved=service.create(input); verify(repository).save(input); verify(assignmentClient).assign(any()); assertSame(input,saved); }
 @Test void retriesAssignmentWhenServiceIsTemporarilyUnavailable(){ Complaint input=new Complaint("Login issue","Cannot login","customer@example.com"); when(repository.save(input)).thenReturn(input); doThrow(new RuntimeException("unavailable")).doNothing().when(assignmentClient).assign(any()); service.create(input); verify(assignmentClient,times(2)).assign(any()); }
 @Test void updatesStatusAndNotifiesCustomer(){ Complaint complaint=new Complaint("Login issue","Cannot login","customer@example.com"); when(repository.findById(1L)).thenReturn(java.util.Optional.of(complaint)); when(repository.save(complaint)).thenReturn(complaint); Complaint saved=service.updateStatus(1L,Complaint.Status.IN_PROGRESS); assertSame(complaint,saved); verify(notificationClient).notify(any()); }
 @Test void retriesStatusNotificationWhenServiceIsTemporarilyUnavailable(){ Complaint complaint=new Complaint("Login issue","Cannot login","customer@example.com"); when(repository.findById(1L)).thenReturn(java.util.Optional.of(complaint)); when(repository.save(complaint)).thenReturn(complaint); doThrow(new RuntimeException("unavailable")).doNothing().when(notificationClient).notify(any()); service.updateStatus(1L,Complaint.Status.IN_PROGRESS); verify(notificationClient,times(2)).notify(any()); }
 @Test void missingComplaintReturnsNotFoundPayload(){ assertEquals("Complaint not found", new ComplaintExceptionHandler().handleMissingComplaint(new NoSuchElementException()).get("error")); }
}
