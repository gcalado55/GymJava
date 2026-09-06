package com.treinoapp.api.controller;

import com.treinoapp.api.dto.MemberRequestDTO;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.service.MemberService;
import jakarta.validation.Valid;
import com.treinoapp.api.dto.DashboardStatsDTO;
import com.treinoapp.api.dto.ProgressOverviewDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

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

    @PatchMapping("/me")
    public ResponseEntity<Member> update(@AuthenticationPrincipal UUID id,
                                         @Valid @RequestBody MemberRequestDTO dto) {
        Member member = memberService.update(id, dto.name(), dto.email());
        return ResponseEntity.ok(member);
    }

    @GetMapping("/me")
    public ResponseEntity<Member> findById(@AuthenticationPrincipal UUID id) {
        Member member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/me/dashboard-stats")
    public ResponseEntity<DashboardStatsDTO> dashboardStats(@AuthenticationPrincipal UUID memberId) {
        return ResponseEntity.ok(memberService.dashboardStats(memberId));
    }

    @GetMapping("/me/progress-overview")
    public ResponseEntity<ProgressOverviewDTO> progressOverview(@AuthenticationPrincipal UUID memberId) {
        return ResponseEntity.ok(memberService.progressOverview(memberId));
    }

}