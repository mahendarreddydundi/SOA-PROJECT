package com.resolvenow.notification;

import static org.mockito.Mockito.*; import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*;
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificationControllerTest { @Mock NotificationRepository repository; @Test void sendsAndStoresAlert(){ Notification notification=new Notification(1L,"user@example.com","Updated"); when(repository.save(notification)).thenReturn(notification); assertSame(notification,new NotificationController(repository).send(notification)); verify(repository).save(notification); } }
