package vn.hieu.jobhunter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hieu.jobhunter.domain.NotificationDTO.NotificationDTO;
import vn.hieu.jobhunter.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

        private final NotificationService notificationService;

        /**
         * Lấy tất cả notification trong 24h gần nhất
         * GET /api/notifications/last24h
         */
        @GetMapping("/last24h")
        public ResponseEntity<List<NotificationDTO>> getNotificationsIn24h() {
                List<NotificationDTO> notifications = notificationService.getAllNotificationsIn24h();
                return ResponseEntity.ok(notifications);
        }
}
