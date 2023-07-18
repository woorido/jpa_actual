package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    Member memberA;
    Member memberB;

    @BeforeEach
    void setup() {
        memberA = new Member("memberA", 10);
        memberB = new Member("memberB", 20);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
    }

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        assertThat(member).isEqualTo(memberRepository.findById(savedMember.getId()).orElse(null));
    }

    @Test
    void CRUD() {

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

    @Test
    void findByUsernameAndAgeGreaterThan() {

        Member findMember = memberRepository.findByUsernameAndAgeGreaterThan("memberB", 15).get(0);

        assertThat(findMember).isEqualTo(memberB);

        //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods
    }

    @Test
    void findTop2By() {
        List<Member> members = memberRepository.findTop2By();

        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    void testNamedQuery() {
        Member findMember = memberRepository.findByUsername("memberA").get(0);

        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    void testQuery() {
        Member findMember = memberRepository.findUser("memberA", 10).get(0);

        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    void findUserNameList() {
        List<String> userNames = memberRepository.findUserNameList();

        assertThat(userNames).contains("memberA", "memberB");
    }

    @Test
    void name() {

        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member memberA = new Member("memberA", 10);
        memberA.changeTeam(teamA);
        memberRepository.save(memberA);

        MemberDto findMember = memberRepository.findMemberDto().get(0);

        assertThat(findMember.getTeamName()).isEqualTo(teamA.getName());
        assertThat(findMember.getUsername()).isEqualTo(memberA.getUsername());
    }
}