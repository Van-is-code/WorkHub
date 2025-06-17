package gr3.workhub.controller;

import gr3.workhub.dto.ReviewDTO;
import gr3.workhub.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Đánh giá công ty", description = "API cho người dùng đánh giá công ty")
@RestController
@RequestMapping("/workhub/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "Người dùng đánh giá công ty", description = "Gửi đánh giá (rating, comment) cho công ty")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Đánh giá thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công ty hoặc người dùng")
    })
    @PostMapping
    public ResponseEntity<Void> createReview(
            @RequestBody ReviewDTO reviewDTO) {
        reviewService.createReview(reviewDTO);
        return ResponseEntity.status(201).build();
    }
}