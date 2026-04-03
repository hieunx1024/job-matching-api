# Kịch Bản Trình Bày & Bảo Vệ Project: JobHunter (Cổng Thông Tin Việc Làm)
*(Góc nhìn: Ứng viên trình bày trước Hội đồng / Nhà tuyển dụng)*

Dạ em chào anh/chị. Hôm nay em xin phép được trình bày về dự án **JobHunter** do em phát triển. Đây là một nền tảng kết nối tuyển dụng đa chiều, được xây dựng theo kiến trúc hiện đại, tập trung vào trải nghiệm người dùng, tính linh hoạt trong quản lý và khả năng mở rộng. 

Sau đây, em xin đi sâu vào 12 điểm cốt lõi của dự án:

---

## 1. Phân tích Yêu cầu Chức năng & Luồng Xử lý Nghiệp vụ
Khi bắt tay vào dự án, em đã phân tích và thiết kế hệ thống tập trung vào 3 tác nhân (actor) chính để tối ưu hóa luồng tương tác:
*   **Với Candidate (Ứng viên):** Mục tiêu là giúp họ tìm việc nhanh chóng và tiện lợi. Em xây dựng luồng từ đăng ký/đăng nhập (hỗ trợ cả Local và Google OAuth2), sau đó là bổ sung hồ sơ và cấu hình kỹ năng. Tính năng nổi bật em làm là **Subscriber (Đăng ký nhận thông báo)** giúp ứng viên nhận email tự động khi có việc làm đúng chuyên môn; cùng với đó là **AI Chatbot** (tích hợp Gemini) đóng vai trò tư vấn viên hướng nghiệp 24/7. Cuối cùng là luồng upload CV nguyên bản dạng PDF để nộp hồ sơ, và theo dõi lịch sử ứng tuyển.
*   **Với HR (Nhà tuyển dụng):** Bài toán đặt ra là xác thực doanh nghiệp và tối ưu chi phí tuyển dụng. Do đó, luồng của HR đi từ việc tạo yêu cầu lập công ty (Chờ Admin duyệt). Khi đã có công ty, HR có thể đăng việc (bị chi phối bởi giới hạn gói cước Subscription), và quản lý hệ thống CV đổ về (chuyển trạng thái: Pending, Reviewing, Approved, Rejected kèm ghi chú phản hồi cho ứng viên).
*   **Với Admin:** Tập trung vào điều hành hệ thống. Admin có quyền tối cao quản lý User, Job, Company, Skill; phê duyệt đăng ký công ty của HR; theo dõi Dashboard thống kê và đặc biệt là quản lý cấu hình phân quyền (Role & Permission).

## 2. Phân tích Yêu cầu Phi Chức năng
Để hệ thống chạy ổn định và an toàn ở thực tế, em chú trọng vào 3 yếu tố:
*   **Hiệu năng (Performance):** Em xử lý phân trang (Pagination) triệt để từ Backend đến Frontend để tránh quá tải tải khi dữ liệu lớn. Khâu tìm kiếm thay vì code tay nhiều mệnh đề `WHERE` phức tạp, em áp dụng thư viện `Specification` kết hợp `turkraft/springfilter`, cho phép filter linh hoạt nhiều tiêu chí (Skill, Location, Tên Job) cùng lúc.
*   **Bảo mật (Security):** Em áp dụng chuẩn Stateless với JWT. Access Token có thời hạn ngắn để tăng tính bảo mật, và Refresh Token được định tuyến lưu trữ trong thẻ **HTTP-only Cookie** nhằm chống lại tấn công XSS (Cross-Site Scripting). Mật khẩu được mã hóa an toàn bằng thuật toán băm Bcrypt.
*   **Dễ mở rộng & Tích hợp (Integration):** Cấu trúc cho phép dễ gán thêm dịch vụ bên thứ ba. Điển hình là em đã tích hợp Storage, Spring Mail xử lý Async (không làm chặn luồng user), VNPAY phục vụ thanh toán, và Spring AI (OpenAI schema với mô hình Gemini) xử lý prompt của Chatbot.

## 3. Thiết kế Cơ Sở Dữ Liệu
Về dữ liệu, em thiết kế chuẩn hóa cho dự án với hệ quản trị CSDL MySQL, sử dụng Hibernate (ORM) để ánh xạ Entity:
*   **Quản lý Tài khoản & Phân quyền:** Gồm bảng `users`, lưới liên kết N-N giữa `roles` và `permissions`.
*   **Quản lý Tuyển dụng:** Bảng `companies`, `company_registrations` (xử lý phê duyệt), `jobs`, `skills` (liên kết N-N qua bảng `job_skill`), và `resumes` (hồ sơ, liên kết 1 User với 1 Job).
*   **Quản lý Thu phí:** Với tư duy SaaS, em thiết kế bảng `subscriptions` (Các gói cước), `user_subscriptions` (Quản lý số bài đã đăng / số bài còn lại), và `payment_history` (lưu lịch sử giao dịch).
*   **Notification/Subscribers:** Bảng `subscribers` theo dõi thông tin nhận bản tin email.

## 4. Xây dựng Cấu trúc Dự án
Dự án được em tách biệt hoàn toàn Frontend và Backend (Mô hình SPA + RESTful API):
*   **Backend (Spring Boot 3 - Java 17):** Em tổ chức project theo kiến trúc phân lớp (Layered Architecture):
    *   `Controller`: Validation dữ liệu và xử lý điều hướng HTTP.
    *   `Service`: Tập trung toàn bộ logic nghiệp vụ (Security, Thanh toán, AI).
    *   `Repository`: Tương tác với Database bằng Spring Data JPA.
    *   `Domain`: Chứa các Entities, DTOs (Request/Response) bọc dữ liệu gọn gàng để trả về Client, tránh lộ thông tin nhạy cảm.
    *   `Config/Util`: Chứa cấu hình bảo mật, xử lý ngoại lệ toàn cục (Global Exception Handler).
*   **Frontend (ReactJS + Vite):** Tổ chức theo Component, sử dụng Redux xử lý State Management (để maintain trạng thái login và quyền hạn), cùng Tailwind/AntD cho giao diện.

## 5. Implement: Đăng Nhập, Đăng Ký, Phân Quyền
Đây là tính năng em làm rất kĩ:
*   **Đăng nhập/Đăng ký:** Em ứng dụng Spring Security 6 với `AuthenticationManager`. Đáng chú ý là tính năng **Google OAuth2** Login. Backend sẽ nhận `ID Token` từ client, kết nối với cổng Google Client API để verify và trả token hệ thống nếu hợp lệ.
*   **Phân quyền Động (Dynamic RBAC):** Thay vì hardcode quyền lực (VD: `hasRole('ADMIN')`), em định nghĩa quyền bằng API Endpoint và HTTP Method (VD: `POST /api/v1/jobs`). Em xây dựng `PermissionInterceptor` để "chặn" mọi request. Nó so khớp URL request với danh sách bảng permission của User. Khớp thì đi tiếp, sai trả mã lỗi 403. Việc này giúp Admin thay đổi quyền hạn động chỉ qua một cú nháy chuột trên UI.

## 6. Kết Nối CSDL & Tính Toàn Vẹn Dữ Liệu
*   Khai báo kết nối an toàn qua `application.properties` với MySQL Dialect.
*   Để bớt đi các đoạn code rườm rà, em dùng Hibernate Event Listeners. Cụ thể là các annotations `@PrePersist` và `@PreUpdate` bên trong các Entities. Nhờ vậy, mỗi khi Save hoặc Update dữ liệu, hệ thống tự động track các trường `createdAt`, `updatedAt`, `createdBy`, `updatedBy` từ ngữ cảnh người dùng đang đăng nhập (SecurityContextHolder).

## 7. Thiết Kế Chuẩn API RESTful
Để dễ dàng maintain và kết nối phía Frontend, em thiết lập nguyên tắc kiến trúc API thống nhất:
*   Naming Convention nhất quán định dạng Noun-based: `GET /api/v1/jobs` (Lấy list), `POST ...` (Tạo).
*   **Standard Wrapper Response:** Em config `ResponseBodyAdvice` để mọi API thành công đều trả về chuẩn một khung (format): `{ statusCode, message, data }`. Việc này giúp phía Frontend viết interceptor bóc tách dữ liệu và bắt lỗi dễ dàng. Việc Validate Request cũng tự động bắt lỗi và báo Message chi tiết qua `@Valid`.

## 8. Module: Người Dùng (User) & Kỹ Năng (Skill)
*   **User Module:** Cho phép tạo tài khoản thủ công hoặc qua Social Register, quản lý thông tin User liên kết chặt với Roll và Company. 
*   **Skill Module:** Cốt lõi của việc matching. Hệ thống lấy ý tưởng "Tagging", HR tạo việc gắn các tags (Java, ReactJS), Candidate cấu hình kĩ năng cũng là tags. Dựa vào đó, Spring Scheduler chạy định kỳ để gửi Email gợi ý việc có tag phù hợp.

## 9. Module: Công Ty (Company)
*   Doanh nghiệp trên sàn. Quy tắc ràng buộc cốt lõi là: *Mỗi User mang Role HR thì chỉ thuộc về 1 Company*. Tác động đến việc query dữ liệu: Khi HR kéo danh sách Job hoặc Resume, hệ thống nhận diện `user.getCompanyId()` từ Token để tự động lọc kết quả của riêng công ty đó.

## 10. Module: Việc Làm (Job) & Pay-Per-Post (Gói cước)
Đây là module em giải bài toán kinh doanh thực tế.
*   Ngoài các field cơ bản (Title, Salary, Location), logic phức tạp nằm ở **giới hạn bài đăng**. Khi HR gửi action `POST /api/v1/jobs`, service của em sẽ kiểm tra user này có bao nhiêu lượt bài quy định trong `UserSubscription`. Nếu hết số lượng, văng Exception `PostLimitExceededException` ngay lập tức. Tính năng này kích hoạt Payment integration ép HR phải mua thêm gói qua VNPAY. Đây là điểm nhấn kinh doanh của hệ thống.

## 11. Tính Năng: Đăng Ký Kiểm Duyệt Công Ty
Để đảm bảo dữ liệu "Sạch", HR không thể tự tạo công ty để đăng bài lung tung:
*   Mô hình Onboarding: Họ phải điền thông tin vào bảng `company_registrations`, upload kèm tài liệu để xác thực `verificationDocument`.
*   Flow: Trạng thái nằm ở "PENDING". Admin thao tác duyệt (Approve) – Hệ thống gán data vào bảng `Company`, set `companyId` cho HR; hoặc "REJECT" (kèm lý do để gửi về HR).

## 12. Module: Ứng Tuyển & Quản Lý CV (Resume)
*   **Candidate:** Tham khảo thông tin Job -> Nhấn Apply -> Upload File PDF (Sử dụng FileService bắt Validate MIME Type, Max Size) -> Sinh dữ liệu bảng `Resume` nối Candidate_ID và Job_ID.
*   **HR:** Review CV. Logic Security ở đây được em rào rất kĩ: HR chỉ có quyền thay đổi Status của Resume nếu `resume.job.company.id == currentUser.company.id`. Kể cả Admin cũng chỉ được phép `Watch`, không can thiệp được vào chuyên môn update CV của công ty đó.

---
**Tổng kết:** Dự án JobHunter được em phát triển không chỉ để thỏa mãn chức năng CRUD cơ bản, mà còn hướng tới tính ứng dụng thực tế với bài toán Phân quyền Mềm, chuẩn hóa luồng phê duyệt, áp dụng giới hạn kinh doanh (Subscription) và đón đầu trào lưu Hỗ trợ người dùng qua AI.

*Em xin cảm ơn ạ! Em sẵn sàng nghe phản biện và trả lời câu hỏi chi tiết.*
