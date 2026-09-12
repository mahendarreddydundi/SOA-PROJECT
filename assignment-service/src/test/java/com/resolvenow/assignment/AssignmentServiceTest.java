package com.resolvenow.assignment;

import static org.mockito.Mockito.*; import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*;
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AssignmentServiceTest { @Mock AssignmentRepository repository; @Mock NotificationClient notifications; @InjectMocks AssignmentService service; @Test void assignmentNotifiesCustomer(){ Assignment assignment=new Assignment(7L,"Support","agent-1"); when(repository.save(any())).thenReturn(assignment); assertSame(assignment,service.assign(7L,"customer@example.com","Support","agent-1")); verify(notifications).notify(any()); } }
