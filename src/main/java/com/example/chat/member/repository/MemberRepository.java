package com.example.chat.member.repository;

import com.example.chat.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByUsername(String username);

    Boolean existsByNickName(String username);

}
