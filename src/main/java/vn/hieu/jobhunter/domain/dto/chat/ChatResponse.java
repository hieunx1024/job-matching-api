package vn.hieu.jobhunter.domain.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatResponse {
    private String reply;
    private List<JobLink> jobs;

    @Data
    @Builder
    public static class JobLink {
        private Long id;
        private String title;
        private String company;
        private String url;
    }
}
