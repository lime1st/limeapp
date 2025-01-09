package lime1st.limeApp.member.presentation.dto;

import lime1st.limeApp.member.application.MemberServiceDTO;

import java.time.LocalDateTime;

public record MemberResponseDTO(
        String memberId,
        String email,
        String username,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    //    서비스 계층 dto 를 프리젠테이션 계층의 dto 로 변경할 때 새로운 인스턴스를 생성하는 단계를 피하고자 static 메서드로 만들었다.
    public static MemberResponseDTO fromService(MemberServiceDTO serviceDTO) {
        return new MemberResponseDTO(
                serviceDTO.memberId(),
                serviceDTO.email(),
                serviceDTO.username(),
                serviceDTO.password(),
                serviceDTO.createdAt(),
                serviceDTO.updatedAt()
        );
    }
}
