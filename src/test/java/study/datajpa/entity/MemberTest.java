package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void name() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        assertThat(members).isEqualTo(Arrays.asList(member1, member2, member3, member4));
    }

    @Test
    void JpaEventBaseEntity() throws Exception {
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist

        Thread.sleep(100);
        member.changeUsername("member2");

        em.flush(); //@PreUpdate
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).orElse(null);

        System.out.println("getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("getLastModifiedDate = " + findMember.getLastModifiedDate());
        System.out.println("getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("getLastModifiedBy = " + findMember.getLastModifiedBy());
    }
}