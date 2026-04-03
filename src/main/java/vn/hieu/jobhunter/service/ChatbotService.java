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
        String systemPrompt = "Bạn là một trợ lý ảo hỗ trợ tìm kiếm việc làm cho hệ thống JobHunter.\n" +
                "Hãy thân thiện, chuyên nghiệp, ngắn gọn và hữu ích.\n" +
                "Nếu người dùng có nhu cầu tìm việc, hãy MỞ công cụ 'searchJobs' để tìm trong cơ sở dữ liệu.\n" +
                "Sau khi có kết quả từ 'searchJobs', hãy trả về câu trả lời giới thiệu một cách lịch sự, format đoạn văn bằng Markdown (KHÔNG cần liệt kê công việc thành danh sách trừ khi cần thiết, vì UI đã hiển thị dạng thẻ Job cards riêng rồi).\n"
                +
                "Nếu không có kết quả từ DB, hãy báo cho họ biết là không tìm thấy.";

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
