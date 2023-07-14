package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    Member memberA;
    Member memberB;

    @BeforeEach
    void setup() {
        memberA = new Member("memberA");
        memberB = new Member("memberB");
    }

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        assertThat(member).isEqualTo(memberRepository.findById(savedMember.getId()).orElse(null));
    }

    @Test
    void CRUD() {
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //단건조회
        Member findMemberA = memberRepository.findById(memberA.getId()).orElse(null);
        Member findMemberB = memberRepository.findById(memberB.getId()).orElse(null);

        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //리스트
        List<Member> members = memberRepository.findAll();

        assertThat(members.size()).isEqualTo(2);

        //카운트
        long count = memberRepository.count();

        assertThat(count).isEqualTo(2);

        //삭제
        memberRepository.delete(memberA);
        memberRepository.delete(memberB);

        long deletedCount = memberRepository.count();

        assertThat(deletedCount).isEqualTo(0);
    }
}