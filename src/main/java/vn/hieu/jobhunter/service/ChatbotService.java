package vn.hieu.jobhunter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import vn.hieu.jobhunter.domain.dto.chat.ChatRequest;
import vn.hieu.jobhunter.domain.dto.chat.ChatResponse;
import vn.hieu.jobhunter.service.ai.JobSearchContext;

import java.util.List;

@Slf4j
@Service
public class ChatbotService {

    private final ChatClient chatClient;

    public ChatbotService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ChatResponse processChat(ChatRequest request) {
        String currentTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
        String systemPrompt = "Bạn là một trợ lý ảo hỗ trợ tìm kiếm việc làm cho hệ thống JobHunter.\n" +
                "Thời gian hiện tại là: " + currentTime + ".\n" +
                "Hãy thân thiện, chuyên nghiệp, ngắn gọn và hữu ích.\n" +
                "NHIỆM VỤ QUAN TRỌNG: Khi người dùng yêu cầu tìm việc (ví dụ: 'tìm việc senior', 'việc làm lương 10tr', 'tìm việc ở Hà Nội'), hãy GỌI NGAY công cụ 'searchJobs' với các tham số bạn trích xuất được.\n" +
                "ĐỪNG hỏi thêm thông tin nếu bạn đã trích xuất được ít nhất một tiêu chí (địa điểm, kỹ năng, lương, hoặc cấp bậc).\n" +
                "Các tham số hỗ trợ:\n" +
                "- location: Địa điểm (VD: Hà Nội, HCM...)\n" +
                "- skill: Tên công việc hoặc kỹ năng (VD: Java, Kế toán...)\n" +
                "- minSalary: Mức lương tối thiểu (Số, VD: 10000000)\n" +
                "- level: Cấp bậc (Phải là một trong: INTERN, FRESHER, JUNIOR, MIDDLE, SENIOR).\n" +
                "Sau khi có kết quả, hãy phản hồi lịch sự bằng Markdown. Nếu không có kết quả, hãy báo cho người dùng và gợi ý họ thay đổi tiêu chí.";

        JobSearchContext.clear();
        try {
            String aiResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(request.getMessage())
                    .functions("searchJobs") // calls the Bean 'searchJobs' auto magic
                    .call()
                    .content();

            List<ChatResponse.JobLink> jobs = JobSearchContext.getJobs();

            return ChatResponse.builder()
                    .reply(aiResponse)
                    .jobs(jobs)
                    .build();
        } catch (Exception e) {
            log.error("Error calling AI", e);
            return ChatResponse.builder()
                    .reply("Xin lỗi, hệ thống AI hiện đang bận hoặc cấu hình API Key chưa chuẩn. Vui lòng thử lại sau giây lát!")
                    .build();
        } finally {
            JobSearchContext.clear();
        }
    }
}
