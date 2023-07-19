package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

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
    void findMemberDto() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member memberA = new Member("memberA", 10);
        memberA.changeTeam(teamA);
        memberRepository.save(memberA);

        MemberDto findMember = memberRepository.findMemberDto().get(0);

        assertThat(findMember.getTeamName()).isEqualTo(teamA.getName());
        assertThat(findMember.getUsername()).isEqualTo(memberA.getUsername());
    }

    @Test
    void findByNames() {
        List<Member> members = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));

        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    void returnType() {
        List<Member> members = memberRepository.findListByUsername("memberA");
        Member member = memberRepository.findMemberByUsername("memberA");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("memberA");

        assertThat(members.get(0)).isEqualTo(memberA);
        assertThat(member).isEqualTo(memberA);
        assertThat(optionalMember.get()).isEqualTo(memberA);
    }

    @Test
    void paging() {
        Member member1 = new Member("member1", 50);
        Member member2 = new Member("member2", 50);
        Member member3 = new Member("member3", 50);
        Member member4 = new Member("member4", 50);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        int age = 50;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(4);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 31));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(30);

        //bulk 연산은 영속성 컨텍스트를 거치지 않고 바로 디비에 적용되기 때문에 후에 로직을을 위해 영속성 컨텍스트를 flush & clear 해주는 것이 좋음 == clearAutomatically = true
        //entityManager.flush();
        //entityManager.clear();

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void queryHint() {
        entityManager.flush();
        entityManager.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("memberA");
        findMember.changeUsername("memberA123");
        memberRepository.flush();
    }

    @Test
    void lock() {
        entityManager.flush();
        entityManager.clear();

        memberRepository.findLockByUsername("memberA");
    }
}