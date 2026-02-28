package com.example.chat.messaging.chat.repository;

import com.example.chat.messaging.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {

}
