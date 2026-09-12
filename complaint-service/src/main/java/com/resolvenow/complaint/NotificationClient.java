package com.resolvenow.complaint;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {
    @PostMapping("/notifications")
    void notify(@RequestBody NotificationRequest request);

    record NotificationRequest(Long complaintId, String recipient, String message) { }
}