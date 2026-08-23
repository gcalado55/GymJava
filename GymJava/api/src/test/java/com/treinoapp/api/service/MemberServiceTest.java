package com.treinoapp.api.service;

import com.treinoapp.api.exception.MemberNotFoundException;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void shouldCreateMember() {
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member result = memberService.create("Gabriel", "gabriel@teste.com");

        assertEquals("Gabriel", result.getName());
        assertEquals("gabriel@teste.com", result.getEmail());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void shouldFindMemberById() {
        UUID id = UUID.randomUUID();
        Member fakeMember = new Member();
        fakeMember.setId(id);
        fakeMember.setName("Gabriel");
        when(memberRepository.findById(id)).thenReturn(Optional.of(fakeMember));

        Member result = memberService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenMemberNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.findById(id));
    }

}