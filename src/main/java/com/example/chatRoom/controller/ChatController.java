package com.example.chatRoom.controller;

import com.example.chatRoom.model.ChatMessage;
import com.example.chatRoom.model.User;
import com.example.chatRoom.repository.ChatMessageRepository;
import com.example.chatRoom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    public ChatController(ChatMessageRepository chatMessageRepository,UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Sauvegarde le message dans la base de données
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Ajouter le nom d'utilisateur dans la session du websocket
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
        return chatMessage;
    }


    @GetMapping("/chat")
    public String getChatMessages(String username,Model model) {

       // boolean isExistingUser = userRepository.existsByUsername(username);

        // Ajouter les attributs nécessaires au modèle
        model.addAttribute("currentUsername", username);
    //    model.addAttribute("isExistingUser", isExistingUser);
       // String currentUsername = setUsername(); // Exemple
        model.addAttribute("currentUsername", username);
        List<ChatMessage> messages = chatMessageRepository.findAll(); // Récupérer tous les messages de la base de données
        model.addAttribute("messages", messages);  // Ajouter les messages au modèle
        return "chat";  // Afficher la vue home.html
    }

}
