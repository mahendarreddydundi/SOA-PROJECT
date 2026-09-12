package com.resolvenow.complaint;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/complaints") public class ComplaintController { private final ComplaintService service; public ComplaintController(ComplaintService service){this.service=service;}
 @GetMapping public List<Complaint> all(){return service.all();} @GetMapping("/{id}") public Complaint one(@PathVariable Long id){return service.one(id);} @PostMapping public Complaint create(@RequestBody Complaint complaint){return service.create(complaint);}
 @PatchMapping("/{id}/status") public Complaint status(@PathVariable Long id,@RequestParam Complaint.Status value){return service.updateStatus(id,value);} @DeleteMapping("/{id}") public void delete(@PathVariable Long id){service.delete(id);}
}
