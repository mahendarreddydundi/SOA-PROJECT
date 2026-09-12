package com.resolvenow.assignment;
import org.springframework.cloud.openfeign.FeignClient; import org.springframework.web.bind.annotation.*;
@FeignClient(name="notification-service") public interface NotificationClient { @PostMapping("/notifications") void notify(@RequestBody NotificationRequest request); record NotificationRequest(Long complaintId,String recipient,String message) {} }
