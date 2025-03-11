package cn.aicamera.backend.controller;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.ImageAnalysis;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/camera")
public class CameraController {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<ImageAnalysis>> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestPart("image") MultipartFile image) throws InterruptedException {
        System.out.println("Receive file: type "+image.getContentType()+" name "+image.getOriginalFilename());
        ImageAnalysis ia = new ImageAnalysis(10,10,10,10);
        return ResponseEntity.ok(new GeneralResponse<>(true,"处理成功",ia));

    }
}
