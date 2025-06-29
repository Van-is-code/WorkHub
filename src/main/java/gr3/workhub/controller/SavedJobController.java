package gr3.workhub.controller;

import gr3.workhub.entity.SavedJob;
import gr3.workhub.service.SavedJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(
        name = "✅ Saved Jobs",
        description = "API cho phép người dùng (ứng viên) lưu và truy xuất danh sách các công việc yêu thích đã lưu."
)
@RestController
@CrossOrigin
@RequestMapping("/workhub/api/v1/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @Operation(
            summary = "<EndPoint cho trang của ứng viên > Lưu công việc",
            description = "Cho phép người dùng lưu một công việc vào danh sách đã lưu của họ."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lưu công việc thành công"),
            @ApiResponse(responseCode = "400", description = "Tham số đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng hoặc công việc")
    })
    @PostMapping
    public ResponseEntity<SavedJob> saveJob(
            HttpServletRequest request,
            @Parameter(description = "ID của công việc cần lưu", required = true)
            @RequestParam Integer jobId) {

        return ResponseEntity.ok(savedJobService.saveJob(request, jobId));
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên > Lấy danh sách công việc đã lưu",
            description = "Truy xuất toàn bộ danh sách các công việc mà người dùng đã lưu."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách công việc đã lưu thành công"),
            @ApiResponse(responseCode = "400", description = "Tham số đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    @GetMapping
    public ResponseEntity<List<SavedJob>> getSavedJobsForUser(HttpServletRequest request) {
        return ResponseEntity.ok(savedJobService.getSavedJobsForUser(request));
    }

    @Operation(
            summary = "<EndPoint cho trang của ứng viên > Bỏ lưu công việc",
            description = "Cho phép người dùng bỏ lưu một công việc khỏi danh sách đã lưu của họ."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bỏ lưu công việc thành công"),
            @ApiResponse(responseCode = "400", description = "Tham số đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng hoặc công việc")
    })
    @DeleteMapping
    public ResponseEntity<Void> unsaveJob(
            HttpServletRequest request,
            @Parameter(description = "ID của công việc cần bỏ lưu", required = true)
            @RequestParam Integer jobId) {
        savedJobService.unsaveJob(request, jobId);
        return ResponseEntity.ok().build();
    }
}