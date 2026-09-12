package com.resolvenow.complaint;

import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ComplaintServiceTest {
 @Mock ComplaintRepository repository; @Mock AssignmentClient assignmentClient; @InjectMocks ComplaintService service;
 @Test void createsComplaintAndRequestsAssignment(){ Complaint input=new Complaint("Login issue","Cannot login","customer@example.com"); Complaint saved=service.create(input); verify(repository).save(input); verify(assignmentClient).assign(any()); assertSame(input,saved); }
}
