package com.example.chat.messaging.chat.domain;

import com.example.chat.messaging.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {

}
