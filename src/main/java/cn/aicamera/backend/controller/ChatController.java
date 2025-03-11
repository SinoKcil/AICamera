package cn.aicamera.backend.controller;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.SuccessResponse;
import cn.aicamera.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

// TODO
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<SuccessResponse> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestParam String message) {
//        chatService.sendMessage(token, message);
        System.out.println("Receive message:"+message);
        return ResponseEntity.ok(new SuccessResponse(true, "消息发送成功"));
    }

//    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> listenMessages(@RequestHeader("Authorization") String token) {
//        return chatService.listenMessages(token);
//    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse> uploadImage(
            @RequestHeader("Authorization") String token,
            @RequestPart("image") MultipartFile image) {
//        chatService.uploadImage(token, image);
        System.out.println("Receive file: type "+image.getContentType()+" name "+image.getOriginalFilename());
        return ResponseEntity.ok(new SuccessResponse(true, "图片上传成功"));
    }
}
