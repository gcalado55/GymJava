package com.treinoapp.api.service;

import com.treinoapp.api.model.Member;
import com.treinoapp.api.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member create(String name, String email) {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        return memberRepository.save(member);
    }

    public Member findById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));
    }

}