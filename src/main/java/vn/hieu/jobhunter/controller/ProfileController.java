package vn.hieu.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hieu.jobhunter.domain.request.ReqProfileDTO;
import vn.hieu.jobhunter.domain.response.user.ResProfileDTO;
import vn.hieu.jobhunter.service.ProfileService;
import vn.hieu.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    @ApiMessage("Get candidate profile")
    public ResponseEntity<ResProfileDTO> getProfile() {
        return ResponseEntity.ok(this.profileService.getProfile());
    }

    @PostMapping
    @ApiMessage("Update candidate profile and CV")
    public ResponseEntity<ResProfileDTO> updateProfile(
            @ModelAttribute ReqProfileDTO reqUpdate
    ) throws Exception {
        return ResponseEntity.ok(this.profileService.updateProfile(reqUpdate));
    }

    @DeleteMapping("/cv/{id}")
    @ApiMessage("Delete candidate CV")
    public ResponseEntity<Void> deleteCv(@org.springframework.web.bind.annotation.PathVariable("id") long cvId) throws Exception {
        this.profileService.deleteUserCv(cvId);
        return ResponseEntity.ok().build();
    }
}
