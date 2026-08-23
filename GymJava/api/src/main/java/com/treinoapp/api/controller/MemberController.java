package com.treinoapp.api.controller;

import com.treinoapp.api.dto.MemberRequestDTO;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Member> create(@Valid @RequestBody MemberRequestDTO dto) {
        Member member = memberService.create(dto.name(), dto.email());
        return ResponseEntity.ok(member);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> findById(@PathVariable UUID id) {
        Member member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }

}