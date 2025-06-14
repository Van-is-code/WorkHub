package gr3.workhub.dto;

import gr3.workhub.entity.Inspection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InspectionStatusResponse {
    private Integer id;
    private Inspection.InspectionStatus inspectionStatus;
}