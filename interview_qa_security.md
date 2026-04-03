# Bộ Câu Hỏi Phản Biện & Trả Lời: Chuyên Sâu Về Security (Authentication & Authorization)

Là một hệ thống kết nối nhiều tác nhân, bảo mật luôn là điểm mà Hội đồng hoặc Nhà tuyển dụng sẽ "xoáy" rất sâu. Dưới đây là các câu hỏi trọng tâm về **Xác thực (Authentication)** và **Phân quyền (Authorization)** cùng cách trả lời khéo léo để ghi điểm:

---

## Phần 1: Xác Thực (Authentication - Đăng nhập / Định danh)

### Câu 1: Tại sao em lại dùng JWT thay vì Session/Cookie truyền thống? Nếu JWT bị lộ thì sao?
*   **Trả lời:** Dạ, em chọn JWT (JSON Web Token) vì ứng dụng của em theo kiến trúc RESTful API (Stateless backend) kết hợp mảng Frontend tách biệt (ReactJS). JWT giúp server không phải lưu trữ State đăng nhập của User, giảm tải cho Database/RAM khi scale hệ thống.
*   **Về việc JWT bị lộ (bị đánh cắp):** Đây là một rủi ro có thật (ví dụ bị đánh cắp qua lỗ hổng XSS). Để hạn chế, em thiết kế luồng Access Token có **thời gian sống (Expiration time) rất ngắn** (ví dụ 15-30 phút). 
    *   Bên cạnh đó, em sử dụng cơ chế **Refresh Token**.
    *   *Điểm nâng cao:* Refresh Token **không** trả về ở trực tiếp dạng chuỗi trong Response Body cho Frontend lưu vào LocalStorage, mà em gài (set) nó vào **HTTP-Only Cookie**. Lúc này, hacker dù có tấn công XSS cũng không thể dùng JavaScript (như `document.cookie`) để lấy được Refresh Token, giúp tài khoản được bảo vệ vững chắc hơn.

### Câu 2: Em nói hệ thống lưu Refresh Token trong DB (`users.refresh_token`). Vậy nếu 1 User đăng nhập trên 2 thiết bị (Máy tính và Điện thoại) thì chuyện gì xảy ra?
*   **Trả lời:** Dạ, với thiết kế hiện tại (lưu 1 Refresh Token dạng String/Text trong bảng `users`), khi User đăng nhập trên thiết bị thứ 2, Refresh Token mới sẽ đè lên Refresh Token cũ. Điều này dẫn đến thiết bị thứ 1 khi hết hạn Access Token sẽ không thể dùng Refresh Token cũ để gia hạn, và bị **đăng xuất (force logout)**. 
*   *Hướng mở rộng (Ghi điểm):* Em nhận thức được đây là hạn chế nếu muốn hỗ trợ đa thiết bị (Multi-devices). Cách nâng cấp đơn giản nhất là em sẽ tạo một bảng riêng `user_tokens` (1 User - N Tokens), mỗi bản ghi lưu Refresh Token kèm theo thông tin thiết bị (User-Agent) và thời hạn.

### Câu 3: Làm thế nào em xử lý luồng tích hợp Đăng nhập bằng Google (Google OAuth2)? Backend làm nhiệm vụ gì ở đây?
*   **Trả lời:** Ở luồng này, Frontend (React) sẽ mở Popup của Google. Khi User cho phép, Google trả về cho Frontend một chuỗi gọi là `ID Token` (không phải Access Token).
    *   Frontend mang `ID Token` này ném xuống API Backend (`POST /api/v1/auth/google`).
    *   Tại Backend, em sử dụng thư viện `google-api-client` để verify cái `ID Token` đó với Server Của Google xem có đúng do Google phát hành không, chữ ký có hợp lệ không.
    *   Nếu hợp lệ, em trích xuất Email và Tên từ Payload. Em check trong Database, nếu Email chưa tồn tại thì tự động tạo User (với Provider là GOOGLE). Cuối cùng, hệ thống sinh ra Access Token + Refresh Token của hệ thống JobHunter trả về. Như vậy, ứng dụng em không bao giờ biết mật khẩu Google của người dùng.

---

## Phần 2: Phân Quyền (Authorization - Quyền truy cập)

### Câu 4: Phân quyền của em là "Phân quyền động" (Dynamic RBAC). Nghĩa là sao? Nó khác gì so với `@PreAuthorize("hasRole('ADMIN')")`?
*   **Trả lời:** Dạ, nếu dùng `@PreAuthorize("hasRole('ADMIN')")` (hardcode trực tiếp trên Controller), mỗi khi doanh nghiệp muốn "Thêm một nhóm quyền mới" (ví dụ: Trợ lý HR, Kế toán), dev sẽ phải **sửa code, build và deploy lại ứng dụng**.
*   **Với Dynamic RBAC của em:** Quyền của người dùng được quyết định ở Database. 1 User có 1 Role. 1 Role mapping (N-N) với nhiều Permission. Mỗi Permission em lưu chính xác **ApiPath** (`/api/v1/jobs`) và **Method** (`POST`).
*   Khi có request đến, em dùng `PermissionInterceptor` (chặn trước khi vào Controller). Interceptor lấy `RequestURI` và `HttpMethod` của client, đối chiếu với danh sách Permission của User hiện tại. Khớp thì chốt cho qua, không khớp thì báo Exception 403. Việc này cho phép Admin **Tạo nhóm quyền mới và tick chọn chức năng trực tiếp trên UI** mà hệ thống không cần dừng/khởi động lại.

### Câu 5: Anh là Admin, anh có thể vào sửa trạng thái hồ sơ CV (thành REJECTED) của công ty em được không?
*   **Trả lời:** Dạ **Không** ạ. Dù Admin có quyền to nhất hệ thống (có Permission chạy API `PUT /api/v1/resumes/{id}`), em đã viết một lớp rào (Business Logic) ở bên trong Controller/Service.
*   Cụ thể ở hàm cập nhật trạng thái Resume, em check nếu Role của User hiện tại đang gọi API là Admin, em sẽ chủ động chặn và báo lỗi `"Admin không được phép cập nhật trạng thái hồ sơ"`. Hệ thống chỉ cho phép User có `company_id` **khớp** với `company_id` của cái Job chứa CV đó mới được phép đổi trạng thái. (Đảm bảo HR công ty nào chỉ được sửa CV của công ty đó).

### Câu 6: Việc so sánh URI trong Interceptor có rủi ro gì nếu API chứa Path Variable (ví dụ: `/api/v1/jobs/5`) không? Làm sao em catch được nó?
*   **Trả lời (Rất quan trọng):** Dạ đây là một câu hỏi rất hay. Khi cấu hình Permission trong DB, Path sẽ là dạng pattern: `/api/v1/jobs/{id}`. Tuy nhiên khi Client gửi lên (`request.getRequestURI()`), chuỗi thực tế sẽ là `/api/v1/jobs/5`.
    *   *Cách xử lý:* Nếu so sánh chuỗi String thông thường `equals` sẽ trật. Trong `PermissionInterceptor` của em, em không dùng `request.getRequestURI()`, mà em sẽ lấy attribute từ Spring: `request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)`. 
    *   Spring đã tự động phân giải request `/api/v1/jobs/5` thành cái pattern gốc lúc map Route (`/api/v1/jobs/{id}`). Từ đó em lấy pattern gốc đem so sánh thẳng `equals` với cấu hình trong Database.

---
## Tóm Lược 3 Ý Ghi Điểm Cao:
1.  **Dùng HTTP-Only Cookie:** Hiểu được tấn công XSS và cách Cookie bảo mật Token.
2.  **Verify Google ID Token ở Backend:** Không mù quáng tin tưởng data từ Client ném xuống mà phải verify với Google Server.
3.  **Interceptor vs HandlerMapping:** Hiểu sâu về luồng đi của Spring Boot (từ Filter -> Dispatcher Servlet -> Interceptor -> Controller) và cách bóc tách được pattern URL động.
