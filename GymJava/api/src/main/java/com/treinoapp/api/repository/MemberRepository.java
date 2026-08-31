package com.treinoapp.api.repository;

import com.treinoapp.api.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}