package lime1st.limeApp.member.infrastructure.entity;

import jakarta.persistence.*;
import lime1st.limeApp.member.application.MemberServiceDTO;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "la_member")
public class MemberEntity {

    @Id
    @Column(name = "member_id")
    private String memberId;
    private String email;
    private String username;
    private String password;
    private boolean enabled;
    private String role;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MemberEntity() {
    }

    public MemberEntity(String memberId, String email, String username, String password, boolean enabled,
                        String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MemberEntity fromService(MemberServiceDTO serviceDTO) {
        return new MemberEntity(
                serviceDTO.memberId(),
                serviceDTO.email(),
                serviceDTO.username(),
                serviceDTO.password(),
                serviceDTO.enabled(),
                serviceDTO.role(),
                serviceDTO.createdAt(),
                serviceDTO.updatedAt()
        );
    }

    public MemberServiceDTO toService() {
        return new MemberServiceDTO(
                this.memberId,
                this.email,
                this.username,
                this.password,
                this.enabled,
                this.role,
                this.createdAt,
                this.updatedAt
        );
    }

    public String getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberEntity that = (MemberEntity) o;
        return getMemberId().equals(that.getMemberId());
    }

    @Override
    public int hashCode() {
        return getMemberId().hashCode();
    }

    @Override
    public String toString() {
        return "MemberEntity{" +
                "memberId='" + memberId + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
