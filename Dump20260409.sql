CREATE DATABASE  IF NOT EXISTS `jobhunter_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `jobhunter_db`;
-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: localhost    Database: jobhunter_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `description` mediumtext,
  `logo` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_verified` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `companies`
--

LOCK TABLES `companies` WRITE;
/*!40000 ALTER TABLE `companies` DISABLE KEYS */;
INSERT INTO `companies` VALUES (1,'Tòa nhà FPT, Số 10 Phạm Văn Bạch, Dịch Vọng, Cầu Giấy, Hà Nội','2026-02-17 12:05:08.063416','admin@gmail.com','Mô tả:\nFPT là tập đoàn công nghệ hàng đầu Việt Nam, hoạt động trong các lĩnh vực phát triển phần mềm, chuyển đổi số, viễn thông và giáo dục. Công ty cung cấp giải pháp công nghệ cho doanh nghiệp toàn cầu, tập trung vào AI, Cloud, Big Data và các hệ thống quy mô lớn với môi trường làm việc chuyên nghiệp, quy trình chuẩn quốc tế.','https://cdn.haitrieu.com/wp-content/uploads/2022/01/Logo-FPT.png','Công ty Cổ phần FPT','2026-02-23 13:17:00.385553','hr@gmail.com',_binary '\0'),(2,'Tầng 6, Tòa nhà Innovation Hub, 27 Nguyễn Văn Cừ, Quận 5, TP. Hồ Chí Minh','2026-02-22 16:28:35.436897','admin@gmail.com','HN Solutions là công ty phát triển phần mềm chuyên cung cấp giải pháp web, mobile và hệ thống quản lý doanh nghiệp. Chúng tôi tập trung vào các sản phẩm sử dụng Spring Boot, ReactJS, Cloud và AI nhằm giúp doanh nghiệp số hóa quy trình và tối ưu vận hành. Môi trường làm việc trẻ, linh hoạt, đề cao kỹ thuật và hiệu suất.','https://media.licdn.com/dms/image/v2/C560BAQHGPSSErOum2w/company-logo_200_200/company-logo_200_200/0/1644842834991?e=2147483647&v=beta&t=7moMtuxOxh2ypb4qjK_RyPjeQaYG4AYN4Te_wpjciCE','Công ty TNHH Công nghệ HN Solutions','2026-02-22 16:44:24.759183','hr1@gmail.com',_binary '\0'),(3,'Tầng 10, Tòa nhà Pearl Plaza, 561A Điện Biên Phủ, Bình Thạnh, TP. Hồ Chí Minh','2026-02-22 16:35:38.793960','admin@gmail.com','NovaTech là công ty sản xuất sản phẩm phần mềm chuyên về nền tảng thương mại điện tử và quản lý doanh nghiệp (ERP, CRM). Đội ngũ tập trung phát triển hệ thống scalable trên Cloud, hướng tới khách hàng khu vực Đông Nam Á với định hướng product-driven.','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTRDaihO2nZUg08TwJluWkUiPMdD7wVibcoBg&s','Công ty Cổ phần Phần mềm NovaTech','2026-02-22 16:45:13.277159','hr2@gmail.com',_binary '\0'),(4,'Tầng 4, Tòa nhà Sunrise City, 25 Nguyễn Hữu Thọ, Quận 7, TP. Hồ Chí Minh','2026-02-22 16:39:51.108882','admin@gmail.com','Digital Horizon cung cấp dịch vụ phát triển phần mềm, UI/UX và chuyển đổi số cho các doanh nghiệp vừa và lớn. Công ty chuyên các giải pháp Web App, Mobile App và tích hợp hệ thống sử dụng NodeJS, Java, React và AWS.','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZNcHOwpwlUJ7WLPcs919BE16q12HF6UhHaw&s','Công ty TNHH Digital Horizon','2026-02-22 16:43:01.707956','hr3@gmail.com',_binary '\0');
/*!40000 ALTER TABLE `companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_registrations`
--

DROP TABLE IF EXISTS `company_registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_registrations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `description` mediumtext,
  `facebook_link` varchar(255) DEFAULT NULL,
  `github_link` varchar(255) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `verification_document` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtfje6e2gesww189ggchxsd2bk` (`user_id`),
  CONSTRAINT `FKtfje6e2gesww189ggchxsd2bk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_registrations`
--

LOCK TABLES `company_registrations` WRITE;
/*!40000 ALTER TABLE `company_registrations` DISABLE KEYS */;
INSERT INTO `company_registrations` VALUES (1,'fpt','fpt','2026-02-17 12:04:20.586334','hr@gmail.com','dssf','sdf','sdf','dssf',NULL,'APPROVED','2026-02-17 12:05:08.066496','admin@gmail.com','sdf',4),(2,'Tầng 6, Tòa nhà Innovation Hub, 27 Nguyễn Văn Cừ, Quận 5, TP. Hồ Chí Minh','Công ty TNHH Công nghệ HN Solutions','2026-02-22 16:28:17.509711','hr1@gmail.com','HN Solutions là công ty phát triển phần mềm chuyên cung cấp giải pháp web, mobile và hệ thống quản lý doanh nghiệp. Chúng tôi tập trung vào các sản phẩm sử dụng Spring Boot, ReactJS, Cloud và AI nhằm giúp doanh nghiệp số hóa quy trình và tối ưu vận hành. Môi trường làm việc trẻ, linh hoạt, đề cao kỹ thuật và hiệu suất.','https://www.facebook.com/hnsolutions.tech','https://github.com/hnsolutions','https://i.imgur.com/6VBx3io.png',NULL,'APPROVED','2026-02-22 16:28:35.445129','admin@gmail.com','https://drive.google.com/file/d/1ABCxyzCompanyLicense/view',5),(3,'Tầng 10, Tòa nhà Pearl Plaza, 561A Điện Biên Phủ, Bình Thạnh, TP. Hồ Chí Minh','Công ty Cổ phần Phần mềm NovaTech','2026-02-22 16:35:30.988787','hr2@gmail.com','NovaTech là công ty sản xuất sản phẩm phần mềm chuyên về nền tảng thương mại điện tử và quản lý doanh nghiệp (ERP, CRM). Đội ngũ tập trung phát triển hệ thống scalable trên Cloud, hướng tới khách hàng khu vực Đông Nam Á với định hướng product-driven.','https://www.facebook.com/novatech.software','https://novatech.vn','https://i.imgur.com/Z6X7K9a.png',NULL,'APPROVED','2026-02-22 16:35:38.796209','admin@gmail.com','https://drive.google.com/file/d/1NovaTechBusinessLicense/view',6),(4,'Tầng 4, Tòa nhà Sunrise City, 25 Nguyễn Hữu Thọ, Quận 7, TP. Hồ Chí Minh','Công ty TNHH Digital Horizon','2026-02-22 16:39:44.968740','hr3@gmail.com','Digital Horizon cung cấp dịch vụ phát triển phần mềm, UI/UX và chuyển đổi số cho các doanh nghiệp vừa và lớn. Công ty chuyên các giải pháp Web App, Mobile App và tích hợp hệ thống sử dụng NodeJS, Java, React và AWS.',' https://www.facebook.com/digitalhorizon.vn','https://github.com/digital-horizon','https://i.imgur.com/8KQwP7L.png',NULL,'APPROVED','2026-02-22 16:39:51.110089','admin@gmail.com','https://drive.google.com/file/d/1DigitalHorizonLicense/view',7);
/*!40000 ALTER TABLE `company_registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_skill`
--

DROP TABLE IF EXISTS `job_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_skill` (
  `job_id` bigint NOT NULL,
  `skill_id` bigint NOT NULL,
  KEY `FKdh76859joo68p6dbj9erh4pbs` (`skill_id`),
  KEY `FKje4q8ajxb3v5bel11dhbxrb8d` (`job_id`),
  CONSTRAINT `FKdh76859joo68p6dbj9erh4pbs` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`),
  CONSTRAINT `FKje4q8ajxb3v5bel11dhbxrb8d` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_skill`
--

LOCK TABLES `job_skill` WRITE;
/*!40000 ALTER TABLE `job_skill` DISABLE KEYS */;
INSERT INTO `job_skill` VALUES (1,1),(1,17),(2,1),(3,1),(3,17),(4,3),(4,4),(4,12),(4,13),(4,15),(5,1),(5,3),(5,4),(6,1),(6,17),(7,1),(9,1),(9,51),(9,52),(9,54),(10,29),(10,30),(10,31),(10,32),(10,33),(10,34),(12,35),(12,44),(12,45),(12,46),(13,1),(13,24),(13,25),(13,27),(13,28),(13,30),(13,39),(13,52),(14,30),(14,31),(14,32),(14,33),(14,34),(14,36),(14,44);
/*!40000 ALTER TABLE `job_skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jobs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `description` mediumtext,
  `end_date` datetime(6) DEFAULT NULL,
  `level` enum('INTERN','FRESHER','JUNIOR','MIDDLE','SENIOR') DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `salary` double NOT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `company_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrtmqcrktb6s7xq8djbs2a2war` (`company_id`),
  CONSTRAINT `FKrtmqcrktb6s7xq8djbs2a2war` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
INSERT INTO `jobs` VALUES (1,_binary '','2026-02-17 15:22:33.572392','hr@gmail.com','lập trình backend hệ thống','2026-03-27 17:00:00.000000','MIDDLE','Hà Nội','java backend',2,20000000,'2026-02-20 17:00:00.000000',NULL,NULL,1),(2,_binary '','2026-02-21 03:21:53.757996','hr@gmail.com','tham gia phát triển hệ thống nội bộ ','2026-03-26 17:00:00.000000','SENIOR','Hồ Chí Minh','senior java developer',2,35000000,'2026-02-22 17:00:00.000000',NULL,NULL,1),(3,_binary '','2026-02-22 16:30:51.258127','hr1@gmail.com','Mô tả công việc:\n\nPhát triển và maintain hệ thống backend cho nền tảng tuyển dụng JobHunter.\n\nThiết kế và xây dựng RESTful API sử dụng Spring Boot.\n\nLàm việc với MySQL, tối ưu query và xử lý dữ liệu lớn.\n\nTích hợp Redis để caching và cải thiện hiệu năng hệ thống.\n\nPhối hợp với team Frontend (ReactJS) để hoàn thiện tính năng.\n\nTham gia review code, tối ưu kiến trúc và đảm bảo chất lượng sản phẩm.\n\nYêu cầu:\n\nTối thiểu 2 năm kinh nghiệm Java backend.\n\nThành thạo Spring Boot, JPA/Hibernate.\n\nHiểu về thiết kế hệ thống, MVC, Clean Architecture.\n\nCó kinh nghiệm làm việc với Git, Docker là lợi thế.\n\nTư duy logic tốt, chủ động xử lý vấn đề.\n\nQuyền lợi:\n\nThưởng dự án + tháng 13.\n\nReview lương 6 tháng/lần.\n\nMôi trường kỹ thuật cao, không OT, tập trung hiệu suất.\n\nHỗ trợ học công nghệ mới, tài trợ khóa học.\n\nLàm việc hybrid, linh hoạt thời gian.','2026-03-10 17:00:00.000000','MIDDLE','P. Hồ Chí Minh (Hybrid 3 ngày onsite)','Backend Java Developer (Spring Boot)',3,18000000,'2026-01-31 17:00:00.000000',NULL,NULL,2),(4,_binary '','2026-02-22 16:33:10.072527','hr1@gmail.com','Mô tả công việc:\n\nPhát triển giao diện người dùng cho hệ thống JobHunter bằng ReactJS.\n\nXây dựng các component tái sử dụng, tối ưu hiệu năng render.\n\nKết nối API từ backend (Spring Boot) bằng fetch/axios.\n\nThiết kế UI responsive, đảm bảo hiển thị tốt trên nhiều thiết bị.\n\nPhối hợp với Backend và UI/UX để hoàn thiện sản phẩm.\n\nTối ưu trải nghiệm người dùng và xử lý các vấn đề cross-browser.\n\nYêu cầu:\n\nCó kinh nghiệm làm việc với ReactJS hoặc dự án cá nhân sử dụng React.\n\nHiểu về component lifecycle, hooks (useState, useEffect…).\n\nThành thạo HTML/CSS, flexbox, grid.\n\nBiết sử dụng TailwindCSS là lợi thế.\n\nCó kiến thức làm việc với RESTful API.\n\nChủ động học hỏi, code rõ ràng, dễ maintain.\n\nQuyền lợi:\n\nThưởng hiệu suất + tháng 13.\n\nĐược mentor về kiến trúc frontend thực tế.\n\nLàm việc hybrid, môi trường trẻ, tập trung kỹ thuật.\n\nReview lương định kỳ 6 tháng.\n\nTham gia trực tiếp vào sản phẩm thực tế, không làm dự án outsource.','2026-03-18 17:00:00.000000','JUNIOR','TP. Hồ Chí Minh (Hybrid)','Frontend Developer (ReactJS)',4,15000000,'2026-02-26 17:00:00.000000',NULL,NULL,2),(5,_binary '','2026-02-23 11:52:52.504482','hr@gmail.com','Mô tả công việc:\n\nPhân tích tài liệu yêu cầu và viết test case, checklist kiểm thử.\n\nThực hiện kiểm thử chức năng hệ thống Web/Mobile.\n\nTest API bằng Postman, kiểm tra dữ liệu bằng SQL.\n\nLog bug, theo dõi và verify lỗi trên hệ thống Jira.\n\nPhối hợp với Dev để tái hiện và xử lý lỗi.\n\nBáo cáo kết quả test hằng ngày cho PM.\n\nYêu cầu:\n\nCó kiến thức về quy trình kiểm thử phần mềm.\n\nBiết viết test case, hiểu các mức độ severity/priority.\n\nCó kinh nghiệm test web hoặc project cá nhân là lợi thế.\n\nCẩn thận, logic tốt, kiên nhẫn khi test.\n\nBiết SQL cơ bản để validate dữ liệu.\n\nQuyền lợi:\n\nĐược đào tạo quy trình QA chuẩn Agile.\n\nCó lộ trình lên Automation Tester.\n\nReview lương định kỳ, thưởng theo hiệu suất.\n\nMôi trường trẻ, tập trung phát triển kỹ năng thực tế.','2026-03-10 17:00:00.000000','JUNIOR','Hà Nội','QA/QC Tester (Manual)',3,14000000,'2026-02-26 17:00:00.000000',NULL,NULL,1),(6,_binary '','2026-02-23 11:55:07.457512','hr1@gmail.com','Mô tả công việc:\n\nTham gia phát triển các module backend đơn giản bằng Spring Boot.\n\nHỗ trợ xây dựng RESTful API và xử lý dữ liệu với MySQL.\n\nViết unit test cơ bản và fix bug theo hướng dẫn của mentor.\n\nTham gia họp daily, làm việc theo mô hình Agile.\n\nHọc và áp dụng các quy chuẩn code, Git workflow của team.\n\nYêu cầu:\n\nSinh viên năm 3, 4 ngành CNTT hoặc liên quan.\n\nNắm vững Java core, OOP.\n\nCó project cá nhân hoặc đã học qua Spring Boot là lợi thế.\n\nCó tinh thần học hỏi, chủ động và trách nhiệm.\n\nQuyền lợi:\n\nĐược mentor 1–1 bởi Senior Developer.\n\nCó cơ hội trở thành nhân viên chính thức sau 3–6 tháng.\n\nTham gia trực tiếp vào dự án thực tế.\n\nHỗ trợ dấu mộc thực tập và tài liệu báo cáo.','2026-03-10 17:00:00.000000','INTERN','TP. Hồ Chí Minh (Hybrid)','Backend Java Intern',4,5000000,'2026-02-24 17:00:00.000000',NULL,NULL,2),(7,_binary '','2026-02-23 12:20:07.550221','hr3@gmail.com','Mô tả công việc:\n\nPhát triển ứng dụng Android cho các hệ thống doanh nghiệp và thương mại điện tử.\n\nXây dựng UI theo Material Design, tối ưu trải nghiệm người dùng.\n\nTích hợp RESTful API với backend (Java/NodeJS).\n\nÁp dụng kiến trúc MVVM, clean code, dễ maintain.\n\nLàm việc với Firebase (Push Notification, Analytics…).\n\nPhối hợp với QA và Backend để hoàn thiện tính năng.\n\nYêu cầu:\n\nCó kinh nghiệm lập trình Android bằng Kotlin.\n\nHiểu Activity/Fragment lifecycle, RecyclerView, Jetpack components.\n\nBiết sử dụng Git và làm việc nhóm.\n\nCó sản phẩm demo hoặc đã publish app là lợi thế.\n\nQuyền lợi:\n\nTham gia phát triển sản phẩm thực tế cho khách hàng doanh nghiệp.\n\nReview lương định kỳ, thưởng hiệu suất.\n\nHỗ trợ đào tạo nâng cao về kiến trúc Mobile.\n\nMôi trường trẻ, quy trình Agile rõ ràng.','2026-03-10 17:00:00.000000','MIDDLE','TP. Hồ Chí Minh (Hybrid)','Android Developer',1,17000000,'2026-02-22 17:00:00.000000',NULL,NULL,4),(8,_binary '','2026-02-23 12:21:08.449961','hr3@gmail.com','Mô tả công việc:\n\nThiết kế kiến trúc và phát triển các ứng dụng Android quy mô lớn.\n\nĐịnh hướng kỹ thuật, review code và hỗ trợ team Mobile.\n\nTối ưu hiệu năng, bộ nhớ và trải nghiệm người dùng.\n\nTích hợp hệ thống backend, bảo mật dữ liệu và xử lý offline sync.\n\nXây dựng CI/CD cho mobile app, quản lý release trên Google Play.\n\nPhối hợp với PM, UI/UX và Backend để đưa ra giải pháp kỹ thuật phù hợp.\n\nYêu cầu:\n\n4+ năm kinh nghiệm phát triển Android bằng Kotlin.\n\nThành thạo Jetpack (ViewModel, Navigation, Room, WorkManager…).\n\nHiểu sâu về Clean Architecture, design pattern.\n\nCó kinh nghiệm tối ưu performance, debugging phức tạp.\n\nĐã từng lead module hoặc mentoring developer.\n\nƯu tiên có sản phẩm publish thực tế trên Google Play.\n\nQuyền lợi:\n\nThu nhập cạnh tranh + thưởng dự án.\n\nQuyền tham gia quyết định kiến trúc kỹ thuật sản phẩm.\n\nTài trợ chứng chỉ, khóa học nâng cao chuyên môn.\n\nMôi trường product-focused, ít hành chính, tập trung kỹ thuật.','2026-03-08 17:00:00.000000','SENIOR','Hà Nội ( OnSite)','Senior Android Developer',1,25000000,'2026-02-22 17:00:00.000000',NULL,NULL,4),(9,_binary '','2026-03-05 14:48:08.226671','hr2@gmail.com','Mô tả công việc\n\nThiết kế và thực hiện test case, test scenario cho hệ thống web và API.\n\nThực hiện manual testing, kiểm tra chức năng, UI, API.\n\nPhát hiện, ghi nhận và theo dõi lỗi trên Jira.\n\nViết test case, test report và phối hợp với developer để fix bug.\n\nThực hiện API testing bằng Postman và kiểm tra dữ liệu bằng SQL.\n\nTham gia review requirement và cải thiện quy trình test.','2026-04-13 17:00:00.000000','MIDDLE','Đà Nẵng','Middle QA Tester',2,18000000,'2026-03-04 17:00:00.000000',NULL,NULL,3),(10,_binary '','2026-03-05 14:50:55.970641','hr2@gmail.com','Mô tả công việc\n\nThiết kế, triển khai và vận hành hạ tầng trên cloud.\n\nQuản lý dịch vụ trên AWS/GCP/Azure.\n\nXây dựng hệ thống CI/CD cho quá trình deploy ứng dụng.\n\nContainer hóa ứng dụng bằng Docker và quản lý bằng Kubernetes.\n\nGiám sát hệ thống, tối ưu hiệu năng và chi phí cloud.\n\nPhối hợp với team DevOps và backend để triển khai hệ thống ổn định.\n\nYêu cầu\n\nKinh nghiệm 2–4 năm làm việc với hệ thống cloud.\n\nThành thạo Linux, Docker, Kubernetes.\n\nCó kinh nghiệm với AWS hoặc GCP.\n\nHiểu về Infrastructure as Code (Terraform).\n\nƯu tiên có chứng chỉ AWS Certified / Cloud Engineer.','2026-03-30 17:00:00.000000','MIDDLE','Cần Thơ','Cloud Engineer',1,23000000,'2026-03-05 17:00:00.000000',NULL,NULL,3),(12,_binary '','2026-03-05 14:52:15.832144','hr2@gmail.com','sđssf','2026-04-20 17:00:00.000000','SENIOR','Hà Nội','Senior Project Manager (PM)',1,40000000,'2026-03-24 17:00:00.000000','2026-03-05 14:55:38.442543','hr2@gmail.com',3),(13,_binary '','2026-03-05 15:02:18.512104','hr3@gmail.com','Mô tả công việc\n\nPhát triển và bảo trì ứng dụng Android.\n\nThiết kế kiến trúc ứng dụng theo MVVM / Clean Architecture.\n\nTích hợp RESTful API với backend.\n\nTối ưu hiệu năng và trải nghiệm người dùng.\n\nPhối hợp với team backend, UI/UX và QA trong quá trình phát triển.\n\nReview code và hỗ trợ các developer trong team.\n\nYêu cầu\n\nKinh nghiệm 4+ năm phát triển Android.\n\nThành thạo Java hoặc Kotlin, Android SDK.\n\nCó kinh nghiệm với REST API, Firebase, Git.\n\nHiểu về design pattern và kiến trúc ứng dụng Android.\n\nCó kinh nghiệm publish app lên Google Play là lợi thế.','2026-04-04 17:00:00.000000','SENIOR','Hồ Chí Minh','Senior Android Developer',1,40000000,'2026-03-04 17:00:00.000000',NULL,NULL,4),(14,_binary '','2026-04-03 15:04:11.813897','hr@gmail.com','Mô tả công việc\n\n1. Trách nhiệm chính (Responsibilities):\n\n    Thiết kế, xây dựng và quản trị hệ thống cơ sở hạ tầng trên nền tảng điện toán đám mây (ưu tiên AWS).\n\n    Vận hành và tối ưu hóa hệ thống containerization (Docker) và orchestration (Kubernetes / Amazon EKS).\n\n    Thiết kế, duy trì và liên tục cải tiến các luồng CI/CD pipelines (sử dụng GitLab CI, Jenkins hoặc GitHub Actions) để tự động hóa quá trình build, test và deploy.\n\n    Triển khai cơ sở hạ tầng dưới dạng mã (Infrastructure as Code - IaC) sử dụng Terraform hoặc Ansible.\n\n    Thiết lập và quản lý hệ thống giám sát hiệu suất (Monitoring & Alerting) bằng Prometheus, Grafana, ELK stack để đảm bảo độ khả dụng cao (high availability) và xử lý sự cố kịp thời.\n\n    Phối hợp chặt chẽ với đội ngũ phát triển (Dev) để tối ưu hóa quy trình phân phối phần mềm và đảm bảo tính bảo mật hệ thống (DevSecOps).\n\n2. Yêu cầu chuyên môn (Requirements):\n\n    Có ít nhất 3 năm kinh nghiệm ở vị trí DevOps Engineer, System Admin hoặc Cloud Engineer.\n\n    Kiến thức chuyên sâu về hệ điều hành Linux và hệ thống mạng (TCP/IP, DNS, Load Balancing).\n\n    Thành thạo ít nhất một ngôn ngữ scripting để tự động hóa tác vụ (Bash, Python, hoặc Go).\n\n    Kinh nghiệm thực chiến với các dịch vụ cốt lõi của AWS (EC2, S3, RDS, VPC, IAM) và kiến trúc Microservices.\n\n    Có tư duy giải quyết vấn đề mạch lạc, khả năng tự học tốt và kỹ năng làm việc nhóm hiệu quả.\n\n3. Quyền lợi (Benefits):\n\n    Cung cấp trang thiết bị làm việc hiện đại (MacBook Pro / Dell XPS).\n\n    Lương tháng 13, thưởng hiệu quả công việc (Performance Bonus) và đánh giá năng lực 2 lần/năm.\n\n    Chế độ làm việc linh hoạt (Hybrid working) và môi trường cởi mở, khuyến khích sáng tạo.\n\n    Gói bảo hiểm sức khỏe toàn diện cao cấp cho cá nhân và hỗ trợ cho người nhà.\n\n    Tài trợ 100% chi phí thi các chứng chỉ quốc tế liên quan (AWS Certified Solutions Architect, CKA, CKAD).','2026-04-30 17:00:00.000000','SENIOR','Hà Nội','Senior DevOps Engineer (AWS / Kubernetes)',1,45000000,'2026-04-02 17:00:00.000000',NULL,NULL,1);
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt05r0b6n0iis8u7dfna4xdh73` (`receiver_id`),
  KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`),
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt05r0b6n0iis8u7dfna4xdh73` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_histories`
--

DROP TABLE IF EXISTS `payment_histories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `subscription_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl18spq89rghok9v9masfh6omx` (`subscription_id`),
  KEY `FKjskvqxf9nbal6nrwe4xmt7127` (`user_id`),
  CONSTRAINT `FKjskvqxf9nbal6nrwe4xmt7127` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKl18spq89rghok9v9masfh6omx` FOREIGN KEY (`subscription_id`) REFERENCES `subscriptions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_histories`
--

LOCK TABLES `payment_histories` WRITE;
/*!40000 ALTER TABLE `payment_histories` DISABLE KEYS */;
INSERT INTO `payment_histories` VALUES (1,500000,'2026-02-21 04:06:17.227311','hr@gmail.com','2026-02-21 04:06:17.227312','VNPAY','PENDING','d1e0cbd3-7055-4c6e-8699-1a8ee97682c5',NULL,NULL,2,4),(2,500000,'2026-02-21 04:06:27.784321','hr@gmail.com','2026-02-21 04:06:27.784321','VNPAY','PENDING','56a0e8b8-431f-4986-a0c4-15ed00395aea',NULL,NULL,2,4),(3,500000,'2026-02-21 04:06:32.157161','hr@gmail.com','2026-02-21 04:06:32.157162','VNPAY','PENDING','dcb19ff5-fd06-46e3-97ff-4f72d94c0639',NULL,NULL,2,4),(4,500000,'2026-02-21 04:08:03.425288','hr@gmail.com','2026-02-21 04:08:03.425288','VNPAY','PENDING','2cb0a495-9771-4288-aeda-cc315395d369',NULL,NULL,2,4),(5,500000,'2026-02-21 04:11:35.188683','hr@gmail.com','2026-02-21 04:11:35.188684','VNPAY','PENDING','1ddad924-4845-4467-a2db-5ea71bb13c34',NULL,NULL,2,4),(6,500000,'2026-02-21 04:19:56.102517','hr@gmail.com','2026-02-21 04:19:56.102517','VNPAY','PENDING','9ae9e3d3-d2ff-4716-8e64-1da4f0b34628',NULL,NULL,2,4),(7,2000000,'2026-02-21 04:20:41.922018','hr@gmail.com','2026-02-21 04:20:41.922018','VNPAY','PENDING','9dd142ae-1b69-4092-bb83-a786283cdb96',NULL,NULL,3,4),(8,500000,'2026-02-22 15:55:53.870177','hr@gmail.com','2026-02-22 15:55:53.870179','VNPAY','PENDING','02398246-4956-4a69-95d8-bbe7aa04187a',NULL,NULL,2,4),(9,500000,'2026-02-22 15:59:54.882900','hr@gmail.com','2026-02-22 15:59:54.882902','VNPAY','PENDING','1ad57fdd-dc29-4fca-b569-7750e125e64b',NULL,NULL,2,4),(10,500000,'2026-02-22 16:01:45.029629','hr@gmail.com','2026-02-22 16:01:45.029631','VNPAY','SUCCESS','75b3e182-0cb2-49e7-b849-b9527ef51e94','2026-02-22 16:05:11.232804','hr@gmail.com',2,4),(11,500000,'2026-02-22 16:05:41.555266','hr@gmail.com','2026-02-22 16:05:41.555268','VNPAY','SUCCESS','95e8d327-d003-453d-a7d6-78043b1ff584','2026-02-22 16:06:21.475833','hr@gmail.com',2,4),(12,500000,'2026-02-23 12:22:44.980255','hr3@gmail.com','2026-02-23 12:22:44.980256','VNPAY','PENDING','af053759-16cc-476c-a44b-a1fb8eb60819',NULL,NULL,2,7),(13,500000,'2026-03-05 14:43:06.077479','hr1@gmail.com','2026-03-05 14:43:06.077482','VNPAY','PENDING','b6b8963e-0fb4-406c-87cf-8f966576a01e',NULL,NULL,2,5),(14,500000,'2026-03-06 02:34:16.866173','hr1@gmail.com','2026-03-06 02:34:16.866174','VNPAY','PENDING','7064d416-64d1-4c13-9a70-377d58453636',NULL,NULL,2,5),(15,500000,'2026-03-28 16:03:55.953019','hr@gmail.com','2026-03-28 16:03:55.953021','VNPAY','SUCCESS','aed8fe0a-4260-4b39-8449-3e42dd4fd4f4','2026-03-28 16:05:58.839301','hr@gmail.com',2,4);
/*!40000 ALTER TABLE `payment_histories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission_role`
--

DROP TABLE IF EXISTS `permission_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission_role` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  KEY `FK6mg4g9rc8u87l0yavf8kjut05` (`permission_id`),
  KEY `FK3vhflqw0lwbwn49xqoivrtugt` (`role_id`),
  CONSTRAINT `FK3vhflqw0lwbwn49xqoivrtugt` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FK6mg4g9rc8u87l0yavf8kjut05` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE;
/*!40000 ALTER TABLE `permission_role` DISABLE KEYS */;
INSERT INTO `permission_role` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),(1,35),(1,36),(1,37),(1,38),(1,39),(1,40),(1,41),(1,42),(1,43),(1,44),(1,45),(1,46),(1,47),(1,48),(2,1),(2,2),(2,3),(2,4),(2,5),(2,17),(2,18),(2,19),(2,20),(2,21),(2,27),(2,28),(2,29),(2,30),(2,31),(4,1),(4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8),(4,9),(4,10),(4,11),(4,17),(4,18),(4,19),(4,20),(4,21),(4,37),(4,38),(4,39),(4,40),(4,41),(4,42),(4,43),(4,44),(5,9),(5,10),(5,11),(5,17),(5,20),(5,21),(5,37),(5,38);
/*!40000 ALTER TABLE `permission_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `api_path` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `module` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'/api/v1/companies','2026-02-16 02:43:14.232382','','POST','COMPANIES','Tạo công ty',NULL,NULL),(2,'/api/v1/companies','2026-02-16 02:43:14.254071','','PUT','COMPANIES','Cập nhật công ty',NULL,NULL),(3,'/api/v1/companies/{id}','2026-02-16 02:43:14.255218','','DELETE','COMPANIES','Xóa công ty',NULL,NULL),(4,'/api/v1/companies/{id}','2026-02-16 02:43:14.256235','','GET','COMPANIES','Lấy thông tin công ty theo ID',NULL,NULL),(5,'/api/v1/companies','2026-02-16 02:43:14.257101','','GET','COMPANIES','Lấy danh sách công ty (phân trang)',NULL,NULL),(6,'/api/v1/jobs','2026-02-16 02:43:14.257930','','POST','JOBS','Tạo công việc',NULL,NULL),(7,'/api/v1/jobs','2026-02-16 02:43:14.258761','','PUT','JOBS','Cập nhật công việc',NULL,NULL),(8,'/api/v1/jobs/{id}','2026-02-16 02:43:14.259543','','DELETE','JOBS','Xóa công việc',NULL,NULL),(9,'/api/v1/jobs/{id}','2026-02-16 02:43:14.260462','','GET','JOBS','Lấy thông tin công việc theo ID',NULL,NULL),(10,'/api/v1/jobs','2026-02-16 02:43:14.261350','','GET','JOBS','Lấy danh sách công việc (phân trang)',NULL,NULL),(11,'/api/v1/jobs/by-created/{username}','2026-02-16 02:43:14.262505','','GET','JOBS','Lấy công việc theo người tạo',NULL,NULL),(12,'/api/v1/permissions','2026-02-16 02:43:14.263378','','POST','PERMISSIONS','Tạo quyền',NULL,NULL),(13,'/api/v1/permissions','2026-02-16 02:43:14.264298','','PUT','PERMISSIONS','Cập nhật quyền',NULL,NULL),(14,'/api/v1/permissions/{id}','2026-02-16 02:43:14.265103','','DELETE','PERMISSIONS','Xóa quyền',NULL,NULL),(15,'/api/v1/permissions/{id}','2026-02-16 02:43:14.266088','','GET','PERMISSIONS','Lấy quyền theo ID',NULL,NULL),(16,'/api/v1/permissions','2026-02-16 02:43:14.267136','','GET','PERMISSIONS','Lấy danh sách quyền (phân trang)',NULL,NULL),(17,'/api/v1/resumes','2026-02-16 02:43:14.268054','','POST','RESUMES','Tạo hồ sơ',NULL,NULL),(18,'/api/v1/resumes','2026-02-16 02:43:14.268871','','PUT','RESUMES','Cập nhật hồ sơ',NULL,NULL),(19,'/api/v1/resumes/{id}','2026-02-16 02:43:14.269672','','DELETE','RESUMES','Xóa hồ sơ',NULL,NULL),(20,'/api/v1/resumes/{id}','2026-02-16 02:43:14.270468','','GET','RESUMES','Lấy hồ sơ theo ID',NULL,NULL),(21,'/api/v1/resumes','2026-02-16 02:43:14.271313','','GET','RESUMES','Lấy danh sách hồ sơ (phân trang)',NULL,NULL),(22,'/api/v1/roles','2026-02-16 02:43:14.272140','','POST','ROLES','Tạo vai trò',NULL,NULL),(23,'/api/v1/roles','2026-02-16 02:43:14.273063','','PUT','ROLES','Cập nhật vai trò',NULL,NULL),(24,'/api/v1/roles/{id}','2026-02-16 02:43:14.274030','','DELETE','ROLES','Xóa vai trò',NULL,NULL),(25,'/api/v1/roles/{id}','2026-02-16 02:43:14.274840','','GET','ROLES','Lấy vai trò theo ID',NULL,NULL),(26,'/api/v1/roles','2026-02-16 02:43:14.275900','','GET','ROLES','Lấy danh sách vai trò (phân trang)',NULL,NULL),(27,'/api/v1/users','2026-02-16 02:43:14.276820','','POST','USERS','Tạo người dùng',NULL,NULL),(28,'/api/v1/users','2026-02-16 02:43:14.277736','','PUT','USERS','Cập nhật người dùng',NULL,NULL),(29,'/api/v1/users/{id}','2026-02-16 02:43:14.278732','','DELETE','USERS','Xóa người dùng',NULL,NULL),(30,'/api/v1/users/{id}','2026-02-16 02:43:14.279946','','GET','USERS','Lấy người dùng theo ID',NULL,NULL),(31,'/api/v1/users','2026-02-16 02:43:14.280805','','GET','USERS','Lấy danh sách người dùng (phân trang)',NULL,NULL),(32,'/api/v1/subscribers','2026-02-16 02:43:14.281706','','POST','SUBSCRIBERS','Tạo người đăng ký',NULL,NULL),(33,'/api/v1/subscribers','2026-02-16 02:43:14.283494','','PUT','SUBSCRIBERS','Cập nhật người đăng ký',NULL,NULL),(34,'/api/v1/subscribers/{id}','2026-02-16 02:43:14.284374','','DELETE','SUBSCRIBERS','Xóa người đăng ký',NULL,NULL),(35,'/api/v1/subscribers/{id}','2026-02-16 02:43:14.285081','','GET','SUBSCRIBERS','Lấy người đăng ký theo ID',NULL,NULL),(36,'/api/v1/subscribers','2026-02-16 02:43:14.285834','','GET','SUBSCRIBERS','Lấy danh sách người đăng ký (phân trang)',NULL,NULL),(37,'/api/v1/files','2026-02-16 02:43:14.286826','','POST','FILES','Tải xuống tệp',NULL,NULL),(38,'/api/v1/files','2026-02-16 02:43:14.287820','','GET','FILES','Tải lên tệp',NULL,NULL),(39,'/api/v1/company-registrations','2026-02-16 02:43:14.288633','','POST','COMPANY_REGISTRATIONS','Tạo yêu cầu đăng ký công ty',NULL,NULL),(40,'/api/v1/company-registrations','2026-02-16 02:43:14.289416','','GET','COMPANY_REGISTRATIONS','Xem danh sách yêu cầu đăng ký công ty',NULL,NULL),(41,'/api/v1/company-registrations/{id}/status','2026-02-16 02:43:14.290432','','PUT','COMPANY_REGISTRATIONS','Cập nhật trạng thái đăng ký công ty',NULL,NULL),(42,'/api/v1/company-registrations/{id}','2026-02-16 02:43:14.291208','','GET','COMPANY_REGISTRATIONS','Lấy thông tin đăng ký công ty theo ID',NULL,NULL),(43,'/api/v1/company-registrations/{id}/reject','2026-02-16 02:43:14.292014','','PUT','COMPANY_REGISTRATIONS','Từ chối đăng ký công ty',NULL,NULL),(44,'/api/v1/company-registrations/{id}','2026-02-16 02:43:14.293063','','DELETE','COMPANY_REGISTRATIONS','Xóa yêu cầu đăng ký công ty',NULL,NULL),(45,'/api/v1/messages','2026-02-16 02:43:14.293928','','POST','MESSAGES','Gửi tin nhắn',NULL,NULL),(46,'/api/v1/messages/{id}','2026-02-16 02:43:14.294729','','DELETE','MESSAGES','Xóa tin nhắn',NULL,NULL),(47,'/api/v1/messages/{id}','2026-02-16 02:43:14.295646','','GET','MESSAGES','Lấy tin nhắn theo ID',NULL,NULL),(48,'/api/v1/messages/conversation','2026-02-16 02:43:14.296395','','GET','MESSAGES','Lấy danh sách tin nhắn giữa 2 người dùng',NULL,NULL);
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resumes`
--

DROP TABLE IF EXISTS `resumes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resumes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `status` enum('PENDING','REVIEWING','APPROVED','REJECTED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `job_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjdec9qbp2blbpag6obwf0fmbd` (`job_id`),
  KEY `FK340nuaivxiy99hslr3sdydfvv` (`user_id`),
  CONSTRAINT `FK340nuaivxiy99hslr3sdydfvv` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjdec9qbp2blbpag6obwf0fmbd` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resumes`
--

LOCK TABLES `resumes` WRITE;
/*!40000 ALTER TABLE `resumes` DISABLE KEYS */;
INSERT INTO `resumes` VALUES (1,'2026-02-17 15:40:33.611557','candidate@gmail.com','candidate@gmail.com','','APPROVED','2026-02-17 16:03:17.493791','hr@gmail.com','1771342833554-NGUYEN-XUAN-HIEU-TopCV.vn-170226.222548.pdf',1,3),(2,'2026-02-17 15:45:17.342731','candidate@gmail.com','candidate@gmail.com','Không đạt yêu cầu.','REJECTED','2026-02-17 16:03:07.248595','hr@gmail.com','1771343117333-NGUYEN-XUAN-HIEU-TopCV.vn-170226.222548.pdf',1,3),(3,'2026-02-19 06:45:05.125340','hr@gmail.com','hr@gmail.com','Không đạt yêu cầu.','REJECTED','2026-02-19 06:45:25.690423','hr@gmail.com','1771483505102-NGUYEN-XUAN-HIEU-TopCV.vn-170226.222548.pdf',1,4),(4,'2026-02-19 08:10:25.162570','hr@gmail.com','hr@gmail.com','Không đạt yêu cầu.','REJECTED','2026-04-03 15:22:06.730296','hr@gmail.com','1771488625125-NGUYEN-XUAN-HIEU-TopCV.vn-170226.222548.pdf',1,4),(5,'2026-02-23 10:11:40.213079','hieuakvip1024@gmail.com','hieuakvip1024@gmail.com','Đã được chấp nhận. Liên hệ ứng viên qua email.','APPROVED','2026-02-23 10:12:02.962182','hr@gmail.com','1771841500163-NGUYEN-XUAN-HIEU-TopCV.vn-170226.222548.pdf',1,2),(6,'2026-04-03 15:05:08.189977','hieuakvip1024@gmail.com','hieuakvip1024@gmail.com','','APPROVED','2026-04-03 15:06:08.085304','hr@gmail.com','1775228708172-NGUYEN-XUAN-HIEU-JAVA-CV.pdf',14,2),(7,'2026-04-03 15:29:04.517779','hieuakvip1024@gmail.com','hieuakvip1024@gmail.com',NULL,'PENDING',NULL,NULL,'1775230144491-NGUYEN-XUAN-HIEU-JAVA-CV.pdf',13,2),(8,'2026-04-03 15:29:59.354282','hieuakvip1024@gmail.com','hieuakvip1024@gmail.com',NULL,'PENDING',NULL,NULL,'1775230199343-NGUYEN-XUAN-HIEU-JAVA-CV.pdf',7,2),(9,'2026-04-03 15:31:33.819622','hieuakvip1024@gmail.com','hieuakvip1024@gmail.com','Đã được chấp nhận. Liên hệ ứng viên qua email.','APPROVED','2026-04-03 15:31:38.637414','hr1@gmail.com','1775230293809-NGUYEN-XUAN-HIEU-JAVA-CV.pdf',3,2);
/*!40000 ALTER TABLE `resumes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,_binary '','2026-02-16 02:43:14.333884','','Quản trị viên có toàn quyền trong hệ thống','SUPER_ADMIN',NULL,NULL),(2,_binary '','2026-02-16 02:43:14.404882','','Người quản lý có quyền với người dùng, công ty và hồ sơ','MANAGER',NULL,NULL),(4,_binary '','2026-02-17 11:20:49.044075','','Nhà tuyển dụng: Đăng tin, quản lý hồ sơ','HR',NULL,NULL),(5,_binary '','2026-02-17 11:20:49.062188','','Ứng viên: Tìm việc, nộp hồ sơ','CANDIDATE',NULL,NULL);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skills`
--

DROP TABLE IF EXISTS `skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skills`
--

LOCK TABLES `skills` WRITE;
/*!40000 ALTER TABLE `skills` DISABLE KEYS */;
INSERT INTO `skills` VALUES (1,'2026-02-17 13:33:22.825318','','Java',NULL,NULL),(2,'2026-02-17 13:33:22.826635','','Python',NULL,NULL),(3,'2026-02-17 13:33:22.827468','','JavaScript',NULL,NULL),(4,'2026-02-17 13:33:22.828263','','TypeScript',NULL,NULL),(5,'2026-02-17 13:33:22.829047','','C++',NULL,NULL),(6,'2026-02-17 13:33:22.829708','','C#',NULL,NULL),(7,'2026-02-17 13:33:22.830428','','PHP',NULL,NULL),(8,'2026-02-17 13:33:22.831052','','Ruby',NULL,NULL),(9,'2026-02-17 13:33:22.831672','','Go',NULL,NULL),(10,'2026-02-17 13:33:22.832350','','Kotlin',NULL,NULL),(11,'2026-02-17 13:33:22.833122','','Swift',NULL,NULL),(12,'2026-02-17 13:33:22.833778','','React',NULL,NULL),(13,'2026-02-17 13:33:22.834439','','Angular',NULL,NULL),(14,'2026-02-17 13:33:22.835725','','Vue.js',NULL,NULL),(15,'2026-02-17 13:33:22.836350','','Next.js',NULL,NULL),(16,'2026-02-17 13:33:22.837167','','Nuxt.js',NULL,NULL),(17,'2026-02-17 13:33:22.837839','','Spring Boot',NULL,NULL),(18,'2026-02-17 13:33:22.838495','','Node.js',NULL,NULL),(19,'2026-02-17 13:33:22.839153','','Express.js',NULL,NULL),(20,'2026-02-17 13:33:22.839777','','Django',NULL,NULL),(21,'2026-02-17 13:33:22.840587','','Flask',NULL,NULL),(22,'2026-02-17 13:33:22.841653','','Laravel',NULL,NULL),(23,'2026-02-17 13:33:22.842685','','ASP.NET',NULL,NULL),(24,'2026-02-17 13:33:22.843559','','MySQL',NULL,NULL),(25,'2026-02-17 13:33:22.844273','','PostgreSQL',NULL,NULL),(26,'2026-02-17 13:33:22.844959','','MongoDB',NULL,NULL),(27,'2026-02-17 13:33:22.845790','','Redis',NULL,NULL),(28,'2026-02-17 13:33:22.846483','','Oracle',NULL,NULL),(29,'2026-02-17 13:33:22.847276','','SQL Server',NULL,NULL),(30,'2026-02-17 13:33:22.848094','','Docker',NULL,NULL),(31,'2026-02-17 13:33:22.848809','','Kubernetes',NULL,NULL),(32,'2026-02-17 13:33:22.850080','','AWS',NULL,NULL),(33,'2026-02-17 13:33:22.851082','','Azure',NULL,NULL),(34,'2026-02-17 13:33:22.851819','','Google Cloud',NULL,NULL),(35,'2026-02-17 13:33:22.852456','','Jenkins',NULL,NULL),(36,'2026-02-17 13:33:22.853003','','GitLab CI/CD',NULL,NULL),(37,'2026-02-17 13:33:22.853697','','React Native',NULL,NULL),(38,'2026-02-17 13:33:22.854456','','Flutter',NULL,NULL),(39,'2026-02-17 13:33:22.855171','','Android',NULL,NULL),(40,'2026-02-17 13:33:22.855784','','iOS',NULL,NULL),(41,'2026-02-17 13:33:22.856387','','Git',NULL,NULL),(42,'2026-02-17 13:33:22.857044','','REST API',NULL,NULL),(43,'2026-02-17 13:33:22.857800','','GraphQL',NULL,NULL),(44,'2026-02-17 13:33:22.858401','','Microservices',NULL,NULL),(45,'2026-02-17 13:33:22.859002','','Agile',NULL,NULL),(46,'2026-02-17 13:33:22.859612','','Scrum',NULL,NULL),(47,'2026-02-17 13:33:22.860257','','UI/UX Design',NULL,NULL),(48,'2026-02-17 13:33:22.860779','','HTML/CSS',NULL,NULL),(49,'2026-02-17 13:33:22.861302','','Tailwind CSS',NULL,NULL),(50,'2026-02-17 13:33:22.861895','','Bootstrap',NULL,NULL),(51,'2026-02-17 13:33:22.862592','','Testing',NULL,NULL),(52,'2026-02-17 13:33:22.863759','','JUnit',NULL,NULL),(53,'2026-02-17 13:33:22.864590','','Jest',NULL,NULL),(54,'2026-02-17 13:33:22.865229','','Selenium',NULL,NULL);
/*!40000 ALTER TABLE `skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber_skill`
--

DROP TABLE IF EXISTS `subscriber_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriber_skill` (
  `subscriber_id` bigint NOT NULL,
  `skill_id` bigint NOT NULL,
  KEY `FKly8pe7rx11g3v97b1oq0vjs2r` (`skill_id`),
  KEY `FKjflpvmqmxox8edvsldr12hqjc` (`subscriber_id`),
  CONSTRAINT `FKjflpvmqmxox8edvsldr12hqjc` FOREIGN KEY (`subscriber_id`) REFERENCES `subscribers` (`id`),
  CONSTRAINT `FKly8pe7rx11g3v97b1oq0vjs2r` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber_skill`
--

LOCK TABLES `subscriber_skill` WRITE;
/*!40000 ALTER TABLE `subscriber_skill` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscriber_skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscribers`
--

DROP TABLE IF EXISTS `subscribers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscribers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscribers`
--

LOCK TABLES `subscribers` WRITE;
/*!40000 ALTER TABLE `subscribers` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscribers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `duration_days` int NOT NULL,
  `limit_posts` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriptions`
--

LOCK TABLES `subscriptions` WRITE;
/*!40000 ALTER TABLE `subscriptions` DISABLE KEYS */;
INSERT INTO `subscriptions` VALUES (1,'2026-02-21 03:30:31.040624','',30,3,'Free',0,'2026-02-21 03:30:31.037935','system'),(2,'2026-02-21 03:30:31.076707','',30,20,'Professional',500000,'2026-02-21 03:30:31.075985','system'),(3,'2026-02-21 03:30:31.084267','',30,-1,'Enterprise',2000000,'2026-02-21 03:30:31.083779','system');
/*!40000 ALTER TABLE `subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_subscriptions`
--

DROP TABLE IF EXISTS `user_subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `remaining_posts` int NOT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `subscription_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `total_posts` int NOT NULL,
  `used_posts` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcrkxok09b5ucoqbd9gpuqy2kb` (`subscription_id`),
  KEY `FK3l40lbyji8kj5xoc20ycwsc8g` (`user_id`),
  CONSTRAINT `FK3l40lbyji8kj5xoc20ycwsc8g` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKcrkxok09b5ucoqbd9gpuqy2kb` FOREIGN KEY (`subscription_id`) REFERENCES `subscriptions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_subscriptions`
--

LOCK TABLES `user_subscriptions` WRITE;
/*!40000 ALTER TABLE `user_subscriptions` DISABLE KEYS */;
INSERT INTO `user_subscriptions` VALUES (1,_binary '\0','2026-02-23 12:05:43.787668','anonymousUser','2026-03-25 12:05:43.785104',20,'2026-02-22 16:01:45.029629','2026-03-28 16:05:58.839454','hr@gmail.com',2,4,20,0),(2,_binary '\0','2026-02-23 12:05:43.811188','anonymousUser','2026-03-25 12:05:43.810557',20,'2026-02-22 16:05:41.555266','2026-03-28 16:05:58.839544','hr@gmail.com',2,4,20,0),(3,_binary '\0','2026-02-23 12:05:43.845675','anonymousUser','2026-03-25 12:05:43.845376',20,'2026-02-22 16:01:45.029629','2026-03-28 16:05:58.839591','hr@gmail.com',2,4,20,0),(4,_binary '\0','2026-02-23 12:05:43.854670','anonymousUser','2026-03-25 12:05:43.854096',20,'2026-02-22 16:05:41.555266','2026-03-28 16:05:58.839629','hr@gmail.com',2,4,20,0),(5,_binary '\0','2026-02-23 12:07:10.194023','anonymousUser','2026-03-25 12:07:10.191507',20,'2026-02-22 16:01:45.029629','2026-03-28 16:05:58.839665','hr@gmail.com',2,4,20,0),(6,_binary '\0','2026-02-23 12:07:10.217659','anonymousUser','2026-03-25 12:07:10.217180',20,'2026-02-22 16:05:41.555266','2026-03-28 16:05:58.839702','hr@gmail.com',2,4,20,0),(7,_binary '\0','2026-02-23 12:07:10.253110','anonymousUser','2026-03-25 12:07:10.252682',20,'2026-02-22 16:01:45.029629','2026-03-28 16:05:58.839738','hr@gmail.com',2,4,20,0),(8,_binary '\0','2026-02-23 12:07:10.260493','anonymousUser','2026-03-25 12:07:10.260064',20,'2026-02-22 16:05:41.555266','2026-03-28 16:05:58.839821','hr@gmail.com',2,4,20,0),(9,_binary '','2026-03-28 16:05:58.833737','hr@gmail.com','2026-04-27 16:05:58.827763',19,'2026-03-28 16:05:58.827760','2026-04-03 15:04:11.801173','hr@gmail.com',2,4,20,1),(10,_binary '','2026-03-28 16:05:58.833726','hr@gmail.com','2026-04-27 16:05:58.827764',20,'2026-03-28 16:05:58.827762',NULL,NULL,2,4,20,0);
/*!40000 ALTER TABLE `user_subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `age` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `refresh_token` mediumtext,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `verification_token` varchar(255) DEFAULT NULL,
  `company_id` bigint DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `job_posting_credits` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKin8gn4o1hpiwe6qe4ey7ykwq7` (`company_id`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  CONSTRAINT `FKin8gn4o1hpiwe6qe4ey7ykwq7` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Hà Nội',25,'2026-02-16 02:43:14.490414','','admin@gmail.com',_binary '\0','MALE','Super Admin','$2a$10$CDHk2OVRuLaxgXu.WH8/iOmlY6SYD.SDr0pAMfsiHaAo0e7QY83ZG','LOCAL',NULL,'2026-03-28 15:54:37.309816','admin@gmail.com',NULL,NULL,1,0),(2,'Hà Nội',25,'2026-02-16 03:12:30.491251','anonymousUser','hieuakvip1024@gmail.com',_binary '\0','MALE','Hiếu Nguyễn','$2a$10$U4M3ZnaDcqILmorfJFcI0e8I5crgT8gKZddvlX.yLpN42C/Xq.w5W','GOOGLE','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoaWV1YWt2aXAxMDI0QGdtYWlsLmNvbSIsImV4cCI6MTc4Mzg2ODIxNCwiaWF0IjoxNzc1MjI4MjE0LCJ1c2VyIjp7ImlkIjoyLCJlbWFpbCI6ImhpZXVha3ZpcDEwMjRAZ21haWwuY29tIiwibmFtZSI6Ikhp4bq_dSBOZ3V54buFbiJ9fQ.O8JsikNzyxOc8fXNiYMEX1G1NW8T-qVjUztObnFpK3aiNleptRKDaecQPmnxX6KmCf9-5nW-83ZZOALjLOV05Q','2026-04-03 14:58:36.943203','hieuakvip1024@gmail.com',NULL,NULL,5,0),(3,'Hà Nội',20,'2026-02-17 09:06:27.657132','anonymousUser','candidate@gmail.com',_binary '\0','MALE','Nguyen Xuan Hieu','$2a$10$CmO.J8f6EnjKnOusO6aHYOLV.Oz6bItfwuoyjPUnYmuor3NgesulC','LOCAL','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjYW5kaWRhdGVAZ21haWwuY29tIiwiZXhwIjoxNzgxMTY5OTk3LCJpYXQiOjE3NzI1Mjk5OTcsInVzZXIiOnsiaWQiOjMsImVtYWlsIjoiY2FuZGlkYXRlQGdtYWlsLmNvbSIsIm5hbWUiOiJOZ3V5ZW4gWHVhbiBIaWV1In19.6RNbp7c6Tj47qYuwhigeFlQRa6jARwLfifcJBCTEeFCAdcr_2VUnruUhjdi2nVh60qq42ZXpfYWDwxLjPAUhlQ','2026-03-03 09:26:37.132414','candidate@gmail.com',NULL,NULL,5,0),(4,'Hà Nội',27,'2026-02-17 11:23:28.721657','anonymousUser','hr@gmail.com',_binary '\0','MALE','Nguyen Van A','$2a$10$0Ml2kZT0YHORPmVkd/2KnOjcRiKwx6pVnH.k4c1WnkBjrnPNwLvTe','LOCAL',NULL,'2026-04-03 15:29:19.682374','hr@gmail.com',NULL,1,4,0),(5,'Hà Nội',28,'2026-02-22 16:20:00.228688','anonymousUser','hr1@gmail.com',_binary '\0','MALE','Nguyen Van B','$2a$10$QVV7kxsLU34/aqlADgNqK.fEAMCAPQ4YBSTvcBMXF9py.yFH1w3gK','LOCAL','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJocjFAZ21haWwuY29tIiwiZXhwIjoxNzgzODcwMTcyLCJpYXQiOjE3NzUyMzAxNzIsInVzZXIiOnsiaWQiOjUsImVtYWlsIjoiaHIxQGdtYWlsLmNvbSIsIm5hbWUiOiJOZ3V5ZW4gVmFuIEIifX0.7JVBxAw637RJx1eVxnEtDmhJ5P52zB7qkBfFDj3hPWCDOQhzu77U6c7vWMre6yBdSqkl_nZbdLb_FCJxdJHkvQ','2026-04-03 15:29:32.340876','hr1@gmail.com',NULL,2,4,0),(6,'Bac Ninh',27,'2026-02-22 16:34:51.966490','anonymousUser','hr2@gmail.com',_binary '\0','FEMALE','Nguyen Thi C','$2a$10$/nwRdgnukA/6dPNOiylmhOXZl4PF/XY2ZjsEH/YvgU/7NcviksPO6','LOCAL',NULL,'2026-04-03 14:56:46.413331','hr2@gmail.com',NULL,3,4,0),(7,'Hoa BinhBinh',32,'2026-02-22 16:36:46.012622','anonymousUser','hr3@gmail.com',_binary '\0','MALE','Nguyen Van D','$2a$10$ycjhFSiUVchvOK6CZYaRUurVan2QekIH/kwyuxxJIMA6IAltbuUXC','LOCAL',NULL,'2026-03-28 15:57:13.769603','hr3@gmail.com',NULL,4,4,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'jobhunter_db'
--

--
-- Dumping routines for database 'jobhunter_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-09 19:07:58
