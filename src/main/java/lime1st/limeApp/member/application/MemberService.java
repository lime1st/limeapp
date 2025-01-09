package lime1st.limeApp.member.application;

import lime1st.limeApp.common.exception.DataSaveException;
import lime1st.limeApp.common.exception.DuplicationException;
import lime1st.limeApp.common.exception.NotFoundException;
import lime1st.limeApp.member.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository<MemberServiceDTO> repository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository<MemberServiceDTO> repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean delete(String memberId, String username) {
        return repository.deleteByMemberIdAndUsername(memberId, username) > 0;
    }

    public MemberServiceDTO update(MemberServiceDTO serviceDTO, String username) {
        var result = repository.findByEmail(serviceDTO.email())
                .orElseThrow(NotFoundException::new)
                .toDomain();
//        id 는 보안을 위해 여기서
        //  TODO: id 를 어디서 가져올 지...
        // serviceDTO.setMemberId(result.getMemberId().id().toString());
//        받은걸 전부 넘기지만 도메인 상에서 필요한 필드만 수정된다.
        var member = result.withUpdate(serviceDTO.toDomain());
        return repository.save(MemberServiceDTO.fromDomain(member))
                .orElseThrow(DataSaveException::new);
    }

    public MemberServiceDTO join(MemberServiceDTO serviceDTO) {
//        이메일 중복 불가
        validateDuplicateEmail(serviceDTO.email());

//        비밀번호 암호화: 암호화 자체는 데이터를 저장할 때 보안성을 보장하는 기술적 요구사항이므로 도메인과 분리된
//        기술적인 세부사항(Technical Detail)로 간주된다.
//        member domain logic...
        var member = Member.create(serviceDTO.email(), serviceDTO.username(),
                passwordEncoder.encode(serviceDTO.password()));
        return repository.save(MemberServiceDTO.fromDomain(member))
                .orElseThrow(DataSaveException::new);
    }

    public List<MemberServiceDTO> findAll(Pageable pageable) {
        Page<MemberServiceDTO> page = repository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))
                ));
        return page.getContent();
    }

    public MemberServiceDTO findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(NotFoundException::new);
    }

    public MemberServiceDTO findById(String memberId) {
        return repository.findByMemberId(memberId)
                .orElseThrow(NotFoundException::new);
    }

//    도메인 중복검사는 일반적으로 도메인 로직으로 간주하지 않으므로 서비스 레이어에 만들었다.
    private void validateDuplicateEmail(String email) {
        repository.findByEmail(email)
                .ifPresent(member-> {
                    throw new DuplicationException();
                });
    }

    //
    private boolean checkExists(String memberId, String username) {
        return repository.existsByMemberIdAndUsername(memberId, username);
    }
}
