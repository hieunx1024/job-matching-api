package vn.hieu.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.UserCv;
import vn.hieu.jobhunter.domain.request.ReqProfileDTO;
import vn.hieu.jobhunter.domain.response.user.ResProfileDTO;
import vn.hieu.jobhunter.repository.UserCvRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.util.SecurityUtil;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserCvRepository userCvRepository;
    private final FileService fileService;

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseURI;

    public ProfileService(UserRepository userRepository, UserCvRepository userCvRepository, FileService fileService) {
        this.userRepository = userRepository;
        this.userCvRepository = userCvRepository;
        this.fileService = fileService;
    }

    public ResProfileDTO getProfile() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return convertToResProfileDTO(user);
    }

    @Transactional
    public ResProfileDTO updateProfile(ReqProfileDTO reqUpdate) throws Exception {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (reqUpdate != null) {
            if (reqUpdate.getName() != null) user.setName(reqUpdate.getName());
            if (reqUpdate.getAddress() != null) user.setAddress(reqUpdate.getAddress());
            
            if (reqUpdate.getGender() != null) {
                String g = reqUpdate.getGender().trim().toUpperCase();
                if (g.equals("NAM") || g.equals("MALE")) {
                    user.setGender(vn.hieu.jobhunter.util.constant.GenderEnum.MALE);
                } else if (g.equals("NỮ") || g.equals("NU") || g.equals("FEMALE")) {
                    user.setGender(vn.hieu.jobhunter.util.constant.GenderEnum.FEMALE);
                } else {
                    try {
                        user.setGender(vn.hieu.jobhunter.util.constant.GenderEnum.valueOf(g));
                    } catch (Exception e) {
                        user.setGender(vn.hieu.jobhunter.util.constant.GenderEnum.OTHER);
                    }
                }
            }
            if (reqUpdate.getAge() != null && reqUpdate.getAge() > 0) user.setAge(reqUpdate.getAge());
            this.userRepository.save(user);
        }

        MultipartFile file = reqUpdate != null ? reqUpdate.getFile() : null;
        if (file != null && !file.isEmpty()) {
            String folder = "resume";
            this.fileService.createDirectory(baseURI + folder);
            String uploadFileUrl = this.fileService.store(file, folder);

            List<UserCv> cvs = this.userCvRepository.findByUserId(user.getId());
            for (UserCv cv : cvs) {
                if (cv.isDefault()) {
                    cv.setDefault(false);
                    this.userCvRepository.save(cv);
                }
            }

            UserCv userCv = new UserCv();
            userCv.setFileName(file.getOriginalFilename());
            userCv.setUrl(uploadFileUrl);
            userCv.setDefault(true);
            userCv.setUser(user);
            this.userCvRepository.save(userCv);
        }

        return convertToResProfileDTO(user);
    }

    private ResProfileDTO convertToResProfileDTO(User user) {
        ResProfileDTO res = new ResProfileDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());

        List<UserCv> ucvs = this.userCvRepository.findByUserId(user.getId());
        List<ResProfileDTO.UserCvDTO> cvDTOs = ucvs.stream().map(cv -> {
            ResProfileDTO.UserCvDTO dto = new ResProfileDTO.UserCvDTO();
            dto.setId(cv.getId());
            dto.setFileName(cv.getFileName());
            dto.setUrl(cv.getUrl());
            dto.setDefault(cv.isDefault());
            return dto;
        }).collect(Collectors.toList());

        res.setCvs(cvDTOs);
        return res;
    }

    @Transactional
    public void deleteUserCv(long cvId) throws Exception {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        java.util.Optional<UserCv> cvOpt = this.userCvRepository.findById(cvId);
        if (cvOpt.isEmpty()) {
            throw new RuntimeException("CV không tồn tại");
        }
        UserCv cv = cvOpt.get();
        if (cv.getUser().getId() != user.getId()) {
            throw new RuntimeException("Bạn không có quyền xóa CV của người khác");
        }
        this.userCvRepository.delete(cv);
    }
}
