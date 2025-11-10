package com.example.chat.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTime {

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
