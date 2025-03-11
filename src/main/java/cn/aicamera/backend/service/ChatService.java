package cn.aicamera.backend.service;

import cn.aicamera.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

// TODO:对接大模型
@Service
public class ChatService {
//    @Autowired
//    private ChatMapper chatMapper;

//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public void sendMessage(String token, String message) {
//        String email = jwtUtil.getUsernameFromToken(token);
//
//    }
//
//    public Flux<String> listenMessages(String token) {
//        String email = jwtUtil.getUsernameFromToken(token);
//        return Flux.fromStream(chatMapper.findMessagesBySender(username).map(Message::getContent);
//    }
//
//    public String uploadImage(String token, MultipartFile image) {
//        String email = jwtUtil.getUsernameFromToken(token);
//
//    }
}
