package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    Member memberA;
    Member memberB;

    @BeforeEach
    void setup() {
        memberA = new Member("memberA");
        memberB = new Member("memberB");
    }

    @Test
    void testMember() {
        Member savedMember = memberJpaRepository.save(memberA);
        Member findMember = memberJpaRepository.findById(savedMember.getId()).orElse(null);

        assertThat(savedMember).isEqualTo(findMember);
    }

    @Test
    void CRUD() {
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        //단건조회
        Member findMemberA = memberJpaRepository.findById(memberA.getId()).orElse(null);
        Member findMemberB = memberJpaRepository.findById(memberB.getId()).orElse(null);

        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //리스트
        List<Member> members = memberJpaRepository.findAll();

        assertThat(members.size()).isEqualTo(2);

        //카운트
        long count = memberJpaRepository.count();

        assertThat(count).isEqualTo(2);

        //삭제
        memberJpaRepository.delete(memberA);
        memberJpaRepository.delete(memberB);

        long deletedCount = memberJpaRepository.count();

        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void paging() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 1;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    void bulkUpdate() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }
}