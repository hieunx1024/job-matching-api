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
                "NHIỆM VỤ QUAN TRỌNG:\n" +
                "1. Khi người dùng yêu cầu tìm việc (ví dụ: 'tìm việc senior', 'lương dưới 20tr', 'tìm việc ở Hà Nội'), hãy GỌI NGAY công cụ 'searchJobs' với các tham số bạn trích xuất được. Chú ý phân biệt lương tối thiểu (minSalary) và lương tối đa (maxSalary).\n" +
                "2. Khi người dùng hỏi về trạng thái đơn ứng tuyển (ví dụ: 'kiểm tra đơn nộp', 'trạng thái ứng tuyển của tôi'), hãy GỌI NGAY công cụ 'getApplicantStatus' with tham số jobTitle nếu có.\n" +
                "3. Khi người dùng hỏi về thông tin công ty (ví dụ: 'thông tin công ty FPT', 'FPT là công ty gì'), hãy GỌI NGAY công cụ 'getCompanyInfo' với tham số companyName.\n" +
                "ĐỪNG hỏi thêm thông tin nếu bạn đã trích xuất được ít nhất một tiêu chí (địa điểm, kỹ năng, lương, hoặc cấp bậc).\n" +
                "Các tham số hỗ trợ cho searchJobs:\n" +
                "- location: Địa điểm (VD: Hà Nội, HCM...)\n" +
                "- skill: Tên công việc hoặc kỹ năng (VD: Java, Kế toán...)\n" +
                "- minSalary: Mức lương tối thiểu (Số, VD: 10000000)\n" +
                "- maxSalary: Mức lương tối đa (Số, VD: 20000000)\n" +
                "- level: Cấp bậc (Phải là một trong: INTERN, FRESHER, JUNIOR, MIDDLE, SENIOR).\n" +
                "- companyName: Tên công ty (VD: FPT, Viettel...)\n" +
                "Sau khi có kết quả, hãy phản hồi lịch sự bằng Markdown. ĐẶC BIỆT LƯU Ý:\n" +
                "- KHÔNG ĐƯỢC dùng bảng Markdown (Markdown Table) vì giao diện không hỗ trợ.\n" +
                "- Hãy dùng danh sách gạch đầu dòng để liệt kê. Giữa các công việc PHẢI CÓ DÒNG TRỐNG để dễ nhìn.\n" +
                "Ví dụ định dạng chuẩn:\n" +
                "- **Java Backend**\n" +
                "  - Công ty: Công ty Cổ phần FPT\n" +
                "  - Trạng thái: Đã chấp nhận\n" +
                "  - Ghi chú: Liên hệ qua email\n\n" +
                "- **Android Developer**\n" +
                "  - Công ty: Công ty TNHH Digital Horizon\n" +
                "  - Trạng thái: Đang chờ";

        JobSearchContext.clear();
        try {
            String aiResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(request.getMessage())
                    .functions("searchJobs", "getApplicantStatus", "getCompanyInfo") // trigger tools via Spring AI Bean
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
                    .reply("Sorry, the AI system is currently busy or the API Key configuration is invalid. Please try again later.")
                    .build();
        } finally {
            JobSearchContext.clear();
        }
    }
}
