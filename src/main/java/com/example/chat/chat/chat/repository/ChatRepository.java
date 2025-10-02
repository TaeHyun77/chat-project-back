package com.example.chat.chat.chat.repository;

import com.example.chat.chat.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {

}
