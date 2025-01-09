package lime1st.limeApp.member.infrastructure;

import lime1st.limeApp.member.application.MemberRepository;
import lime1st.limeApp.member.application.MemberServiceDTO;
import lime1st.limeApp.member.infrastructure.entity.MemberEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

/*
 * Service 를 테스트하기 위한 Stub 객체
 * */
public class StubMemberRepositoryMEM implements MemberRepository<MemberServiceDTO> {

    private static final Logger log = LoggerFactory.getLogger(StubMemberRepositoryMEM.class);

    private final Map<String, MemberEntity> memberMap = new HashMap<>();

    public StubMemberRepositoryMEM() {
        memberMap.put("fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                new MemberEntity("fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                        "alice@mail.com",
                        "alice",
                        "password",
                        true,
                        "USER",
                        null, null)
        );
        memberMap.put("fc7b9203-f569-46d2-8bf0-9e23f2a131bg",
                new MemberEntity("fc7b9203-f569-46d2-8bf0-9e23f2a131bg",
                        "bob@mail.com",
                        "bob",
                        "password",
                        false,
                        "USER",
                        null, null)
        );
        memberMap.put("fc7b9203-f569-46d2-8bf0-9e23f2a131bh",
                new MemberEntity("fc7b9203-f569-46d2-8bf0-9e23f2a131bh",
                        "john@mail.com",
                        "john",
                        "password",
                        true,
                        "ADMIN",
                        null, null)
        );
    }

    @Override
    public int deleteByMemberIdAndUsername(String memberId, String username) {
        if (memberMap.get(memberId).getUsername().equals(username)) {
            memberMap.remove(memberId);
            return 1;
        }
        return 0;
    }

    @Override
    public Optional<MemberServiceDTO> save(MemberServiceDTO member) {
        //  put 메서드는 기존의 키에 대한 값을 반환하므로 처음 데이터를 삽입할 때 주의!!
        memberMap.put(member.memberId(), MemberEntity.fromService(member));
        return Optional.of(memberMap.get(member.memberId()).toService());
    }

    @Override
    public Optional<MemberServiceDTO> findByEmail(String email) {
        return memberMap.values().stream()
                .filter(member->member.getEmail().equals(email))
                .map(MemberEntity::toService)
                .findAny();
    }

    @Override
    public Optional<MemberServiceDTO> findByUsername(String username) {
        return memberMap.values().stream()
                .filter(member->member.getUsername().equals(username))
                .map(MemberEntity::toService)
                .findAny();
    }

    @Override
    public Page<MemberServiceDTO> findAll(Pageable pageable) {
        //  Map -> List
        List<MemberServiceDTO> memberList = new ArrayList<>(memberMap.values().stream()
                .map(MemberEntity::toService).toList());

        //  정렬 처리를 위한 Comparator 구현
        if (pageable.getSort().isSorted()) {
            Comparator<MemberServiceDTO> comparator = pageable.getSort().stream()
                    .map(sort -> {
                        Comparator<MemberServiceDTO> singleComparator = Comparator.comparing(member->{
                            if (sort.getProperty().equalsIgnoreCase("username")) {
                                return member.username();
                            } else if (sort.getProperty().equalsIgnoreCase("email")) {
                                return member.email();
                            } else {
                                return member.memberId();
                            }
                        });

                        return sort.isAscending() ? singleComparator : singleComparator.reversed();
                    })
                    .reduce(Comparator::thenComparing)
                    .orElse(Comparator.comparing(MemberServiceDTO::memberId)); // 기본 정렬 기준
            memberList = memberList.stream().sorted(comparator).toList();
        }

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), memberList.size());
        List<MemberServiceDTO> pageContent = (start > memberList.size()) ? Collections.emptyList() :
                memberList.stream().toList().subList(start, end);

        return new PageImpl<>(pageContent, pageable, memberList.size());
    }

    @Override
    public Optional<MemberServiceDTO> findByMemberId(String memberId) {
        return memberMap.values().stream()
                .filter(member->member.getMemberId().equals(memberId))
                .map(MemberEntity::toService)
                .findAny();
    }

    @Override
    public boolean existsByMemberIdAndUsername(String memberId, String username) {
        if (memberMap.get(memberId) == null) {
            return false;
        }
        return memberMap.get(memberId).getUsername().equals(username);
    }
}
