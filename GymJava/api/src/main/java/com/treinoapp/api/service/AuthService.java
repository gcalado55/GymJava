package com.treinoapp.api.service;

import com.treinoapp.api.config.JwtUtil;
import com.treinoapp.api.dto.auth.AuthResponseDTO;
import com.treinoapp.api.dto.auth.LoginRequestDTO;
import com.treinoapp.api.dto.auth.RegisterRequestDTO;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (memberRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Member member = new Member();
        member.setName(dto.name());
        member.setEmail(dto.email());
        member.setPassword(passwordEncoder.encode(dto.password()));
        member.setRole("ALUNO");
        
        member = memberRepository.save(member);

        String token = jwtUtil.generateToken(member.getId(), member.getEmail(), member.getRole());
        return new AuthResponseDTO(token, member.getId().toString(), member.getName(), member.getEmail(), member.getRole());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Member member = memberRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.password(), member.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(member.getId(), member.getEmail(), member.getRole());
        return new AuthResponseDTO(token, member.getId().toString(), member.getName(), member.getEmail(), member.getRole());
    }
}
