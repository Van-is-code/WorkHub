package gr3.workhub.controller;

import gr3.workhub.entity.UserPackageHistory;
import gr3.workhub.service.UserPackageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/user-package-histories")
@RequiredArgsConstructor
public class UserPackageHistoryController {

    private final UserPackageHistoryService userPackageHistoryService;

    @GetMapping
    public ResponseEntity<List<UserPackageHistory>> getAllHistories() {
        return ResponseEntity.ok(userPackageHistoryService.getAllHistories());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPackageHistory>> getHistoriesByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(userPackageHistoryService.getHistoriesByUserId(userId));
    }
}