package vn.hieu.jobhunter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hieu.jobhunter.domain.dto.chat.ChatRequest;
import vn.hieu.jobhunter.domain.dto.chat.ChatResponse;
import vn.hieu.jobhunter.service.ChatbotService;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        if (chatRequest.getMessage() == null || chatRequest.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ChatResponse response = chatbotService.processChat(chatRequest);
        return ResponseEntity.ok(response);
    }

    private final vn.hieu.jobhunter.repository.JobRepository jobRepository;

    @GetMapping("/debug-jobs")
    public org.springframework.http.ResponseEntity<?> debugJobs() {
        return org.springframework.http.ResponseEntity.ok(jobRepository.findAll().stream()
                .map(j -> j.getName() + " - Skills: " + j.getSkills().stream().map(s -> s.getName()).toList())
                .toList());
    }
}
