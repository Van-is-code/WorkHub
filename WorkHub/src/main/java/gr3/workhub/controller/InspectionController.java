package gr3.workhub.controller;

import gr3.workhub.dto.InspectionAdminResponse;
import gr3.workhub.dto.InspectionRequest;
import gr3.workhub.dto.InspectionStatusResponse;
import gr3.workhub.entity.Inspection;
import gr3.workhub.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/inspections")
public class InspectionController {
    @Autowired
    private InspectionService inspectionService;

    @PostMapping
    public Inspection createInspection(@RequestParam Integer userId, @RequestBody InspectionRequest request) {
        return inspectionService.createInspection(userId, request);
    }


    @GetMapping("/candidate/{userId}")
    public List<InspectionStatusResponse> getCandidateInspections(@PathVariable Integer userId) {
        return inspectionService.getInspectionsByUser(userId)
                .stream()
                .map(i -> new InspectionStatusResponse(i.getId(), i.getInspectionStatus()))
                .toList();
    }


    @GetMapping("/admin")
    public List<InspectionAdminResponse> getAdminInspections(@RequestParam Inspection.InspectionStatus status) {
        return inspectionService.getInspectionsByStatus(status)
                .stream()
                .map(InspectionAdminResponse::from)
                .toList();
    }


    @PutMapping("/{inspectionId}/status")
    public void updateInspectionStatus(
            @PathVariable Integer inspectionId,
            @RequestParam Inspection.InspectionStatus status) {
        inspectionService.updateInspectionStatus(inspectionId, status);
    }
}