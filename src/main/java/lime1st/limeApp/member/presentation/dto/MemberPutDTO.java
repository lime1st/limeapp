package lime1st.limeApp.member.presentation.dto;

import lime1st.limeApp.member.application.MemberServiceDTO;

public record MemberPutDTO(
        String memberId,
        String email,
        String username,
        String password,
        boolean enabled,
        String role
) {
    public MemberServiceDTO toService() {
        return new MemberServiceDTO(
                this.memberId,
                this.email,
                this.username,
                this.password,
                this.enabled,
                this.role,
                null,
                null
        );
    }
}
