package lime1st.limeApp.jwt.dto;

import java.util.HashMap;
import java.util.Map;

public record JwtClaims(
        String memberId,
        String email,
        String username,
        String role
        ) {

    //  jwt 발행을 위한 항목
    public Map<String, Object> getDataMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", this.memberId);
        map.put("email", this.email);
        map.put("username", this.username);
        map.put("role", this.role);
        return map;
    }
}
