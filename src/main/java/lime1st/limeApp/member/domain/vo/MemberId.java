package lime1st.limeApp.member.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record MemberId(
        UUID id
) {

    public static MemberId withId(String id) {
        return new MemberId(UUID.fromString(id));
    }

    public static MemberId withoutId() {
        return new MemberId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberId memberId = (MemberId) o;
        return Objects.equals(id, memberId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
