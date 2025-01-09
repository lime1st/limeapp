//package lime1st.limeApp.member.infrastructure;
//
//import jakarta.persistence.EntityManager;
//import lime1st.limeApp.member.application.MemberRepository;
//import lime1st.limeApp.member.application.MemberServiceDTO;
//import lime1st.limeApp.member.infrastructure.entity.MemberEntity;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.util.*;
//
//public class MemberRepositoryImplJPA implements MemberRepository<MemberServiceDTO> {
//
//    private final EntityManager em;
//
//    public MemberRepositoryImplJPA(EntityManager em) {
//        this.em = em;
//    }
//
//    @Override
//    public int deleteByMemberIdAndUsername(String memberId, String username) {
//        var memberEntity = findByMemberId(memberId);
//        em.remove(memberEntity);
//        return 1;
//    }
//
//    @Override
//    public Optional<MemberServiceDTO> save(MemberServiceDTO serviceDTO) {
//        var memberEntity = MemberEntity.fromService(serviceDTO);
//        em.persist(memberEntity);
//        return Optional.of(memberEntity.toService());
//    }
//
//    public Optional<MemberServiceDTO> update(MemberServiceDTO serviceDTO) {
//        var memberEntity = MemberEntity.fromService(serviceDTO);
//        em.merge(memberEntity);
//        return Optional.of(memberEntity.toService());
//    }
//
//    @Override
//    public Optional<MemberServiceDTO> findByMemberId(String memberId) {
//        var memberEntity = em.find(MemberEntity.class, memberId);
//        return Optional.of(memberEntity.toService());
//    }
//
//    @Override
//    public Optional<MemberServiceDTO> findByUsername(String username) {
////        JPQL is not SQL, JPQL 은 객체를 탐색!!!!!!
//        var result = em.createQuery("select me from MemberEntity me where me.username = :username",
//                        MemberEntity.class)
//                .setParameter("username", username)
//                .getResultList();
//        return result.stream()
//                .map(MemberEntity::toService)
//                .findAny();
//    }
//
//    @Override
//    public Optional<MemberServiceDTO> findByEmail(String email) {
//        var result = em.createQuery("select me from MemberEntity me where me.email = :email",
//                MemberEntity.class)
//                .setParameter("email", email)
//                .getResultList();
//        return result.stream()
//                .map(MemberEntity::toService)
//                .findAny();
//    }
//
//    @Override
//    public Page<MemberServiceDTO> findAll(Pageable pageable) {
//
//        List<MemberServiceDTO> memberList = em.createQuery("select me from MemberEntity me", MemberEntity.class)
//                .getResultList().stream()
//                .map(MemberEntity::toService)
//                .toList();
//
//        //  정렬 처리를 위한 Comparator 구현
//        if (pageable.getSort().isSorted()) {
//            Comparator<MemberServiceDTO> comparator = pageable.getSort().stream()
//                    .map(sort -> {
//                        Comparator<MemberServiceDTO> singleComparator = Comparator.comparing(member->{
//                            if (sort.getProperty().equalsIgnoreCase("username")) {
//                                return member.username();
//                            } else if (sort.getProperty().equalsIgnoreCase("email")) {
//                                return member.email();
//                            } else {
//                                return member.memberId();
//                            }
//                        });
//
//                        return sort.isAscending() ? singleComparator : singleComparator.reversed();
//                    })
//                    .reduce(Comparator::thenComparing)
//                    .orElse(Comparator.comparing(MemberServiceDTO::memberId)); // 기본 정렬 기준
//            memberList = memberList.stream().sorted(comparator).toList();
//        }
//
//        // 페이징 처리
//        int start = (int) pageable.getOffset();
//        int end = Math.min(start + pageable.getPageSize(), memberList.size());
//        List<MemberServiceDTO> pageContent = (start > memberList.size()) ? Collections.emptyList() :
//                memberList.stream().toList().subList(start, end);
//
//        return new PageImpl<>(pageContent, pageable, memberList.size());
//    }
//
//    @Override
//    public boolean existsByMemberIdAndUsername(String memberId, String username) {
//        var count = em.createQuery(
//                "select count(me) from MemberEntity me where me.member_id = :memberId and me.username = :username",
//                Long.class)
//                .setParameter("member_id", memberId)
//                .setParameter("username", username)
//                .getSingleResult();
//        return count > 0;
//    }
//}
