package lime1st.limeApp.member.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lime1st.limeApp.common.exception.NotFoundException;
import lime1st.limeApp.member.application.MemberRepository;
import lime1st.limeApp.member.application.MemberServiceDTO;
import lime1st.limeApp.member.infrastructure.entity.MemberEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*
* Service 를 테스트하기 위한 Stub 객체
* */
public class StubMemberRepositoryFILE implements MemberRepository<MemberServiceDTO> {

    private static final Logger log = LoggerFactory.getLogger(StubMemberRepositoryFILE.class);

    private final ObjectMapper objectMapper;
    private final File file;
    private Map<String, MemberEntity> memberMap;

    public StubMemberRepositoryFILE() throws IOException {
        this.objectMapper = new ObjectMapper();
        this.file = new File("src/test/resources/lime1st/limeApp/member/member.json");
        this.memberMap = new HashMap<>();
        if (file.exists()) {
            memberMap = objectMapper.readValue(file, new TypeReference<Map<String, MemberEntity>>() {});
        }

        // 테스트 전 파일 초기화, MemberRepository 에 clear() 메소드를 만들면
        // 실제 구현체에서도 사용하지 않는 메소드를 오버라이드 해야 해서 생성자에서 초기화를 함.
        memberMap.clear();

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

        objectMapper.writeValue(file, memberMap);
    }

    @Override
    public int deleteByMemberIdAndUsername(String memberId, String username) {
        if (memberMap.get(memberId).getUsername().equals(username)) {
            try {
                memberMap.remove(memberId);

                objectMapper.writeValue(file, memberMap);
            } catch (IOException e) {
                log.error("An error occurred while processing the file: {}", e.getMessage(), e);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public Optional<MemberServiceDTO> save(MemberServiceDTO serviceDTO) {
        try {
            memberMap.put(serviceDTO.memberId(), MemberEntity.fromService(serviceDTO));

            objectMapper.writeValue(file, memberMap);
        } catch (IOException e) {
            log.error("An error occurred while processing the file: {}", e.getMessage(), e);
        }
        return findByMemberId(serviceDTO.memberId());
    }

    @Override
    public Optional<MemberServiceDTO> findByMemberId(String memberId) {
        return Optional.ofNullable(memberMap.get(memberId))
                .map(MemberEntity::toService);
    }

    @Override
    public Optional<MemberServiceDTO> findByUsername(String username) {
        return memberMap.values().stream()
                .filter(member->member.getUsername().equals(username))
                .map(MemberEntity::toService)
                .findAny();
    }

    @Override
    public Optional<MemberServiceDTO> findByEmail(String email) {
        return memberMap.values().stream()
                .filter(member->member.getEmail().equals(email))
                .map(MemberEntity::toService)
                .findAny();
    }

    @Override
    public Page<MemberServiceDTO> findAll(Pageable pageable) {
        //  Map -> List
        List<MemberEntity> memberList = new ArrayList<>(memberMap.values().stream().toList());

        //  정렬 처리를 위한 Comparator 구현
        if (pageable.getSort().isSorted()) {
            Comparator<MemberEntity> comparator = pageable.getSort().stream()
                    .map(sort -> {
                        Comparator<MemberEntity> singleComparator = Comparator.comparing(member->{
                            if (sort.getProperty().equalsIgnoreCase("username")) {
                                return member.getUsername();
                            } else if (sort.getProperty().equalsIgnoreCase("email")) {
                                return member.getEmail();
                            } else {
                                return member.getMemberId();
                            }
                        });

                        return sort.isAscending() ? singleComparator : singleComparator.reversed();
                    })
                    .reduce(Comparator::thenComparing)
                    .orElse(Comparator.comparing(MemberEntity::getMemberId)); // 기본 정렬 기준
            memberList = memberList.stream().sorted(comparator).toList();
        }

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), memberList.size());
        List<MemberServiceDTO> pageContent = (start > memberList.size()) ? Collections.emptyList() :
                memberList.stream().map(MemberEntity::toService).toList().subList(start, end);

        return new PageImpl<>(pageContent, pageable, memberList.size());
    }

    @Override
    public boolean existsByMemberIdAndUsername(String memberId, String username) {
        if (memberMap.get(memberId) == null) {
            return false;
        }
        return memberMap.get(memberId).getUsername().equals(username);
    }
}
