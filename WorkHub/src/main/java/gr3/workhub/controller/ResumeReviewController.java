package gr3.workhub.controller;

import gr3.workhub.dto.ResumeReviewDTO;
import gr3.workhub.entity.ResumeReview;
import gr3.workhub.service.ResumeReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Đánh giá CV", description = "API cho nhà tuyển dụng đánh giá CV ứng viên")
@RestController
@RequestMapping("/workhub/api/v1/resume-reviews")
@RequiredArgsConstructor
public class ResumeReviewController {
    private final ResumeReviewService resumeReviewService;

    @Operation(summary = "Nhà tuyển dụng đánh giá CV", description = "Tạo đánh giá mới cho CV ứng viên")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo đánh giá thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping
    public ResponseEntity<ResumeReview> createReview(@RequestBody ResumeReviewDTO dto) {
        return ResponseEntity.status(201).body(resumeReviewService.createReview(dto));
    }

    @Operation(summary = "Nhà tuyển dụng cập nhật đánh giá", description = "Cập nhật đánh giá CV đã tạo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đánh giá")
    })
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResumeReview> updateReview(
            @Parameter(description = "ID đánh giá", required = true) @PathVariable Integer reviewId,
            @RequestBody ResumeReviewDTO dto) {
        return ResponseEntity.ok(resumeReviewService.updateReview(reviewId, dto));
    }

    @Operation(summary = "Lấy đánh giá theo ứng viên", description = "Lấy tất cả đánh giá CV của một ứng viên")
    @ApiResponse(responseCode = "200", description = "Lấy thành công")
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ResumeReview>> getReviewsByCandidate(
            @Parameter(description = "ID ứng viên", required = true) @PathVariable Integer candidateId) {
        return ResponseEntity.ok(resumeReviewService.getReviewsByCandidateId(candidateId));
    }

    @Operation(summary = "Lấy tất cả đánh giá", description = "Lấy toàn bộ đánh giá CV")
    @ApiResponse(responseCode = "200", description = "Lấy thành công")
    @GetMapping
    public ResponseEntity<List<ResumeReview>> getAllReviews() {
        return ResponseEntity.ok(resumeReviewService.getAllReviews());
    }
}