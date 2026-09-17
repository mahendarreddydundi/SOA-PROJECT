package com.resolvenow.complaint;
import org.springframework.cloud.openfeign.FeignClient; import org.springframework.web.bind.annotation.*;
@FeignClient(name="assignment-service") public interface AssignmentClient { @PostMapping("/assignments") void assign(@RequestBody AssignmentRequest request); record AssignmentRequest(Long complaintId,String customerEmail) {} }
