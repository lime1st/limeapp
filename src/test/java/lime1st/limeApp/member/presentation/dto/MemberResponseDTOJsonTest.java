package lime1st.limeApp.member.presentation.dto;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class MemberResponseDTOJsonTest {

    @Autowired
    private JacksonTester<MemberResponseDTO> json;

    @Autowired
    private JacksonTester<MemberResponseDTO[]> jsonList;

    private MemberResponseDTO[] responseDTOS;

    @BeforeEach
    void setUp() {
        responseDTOS = Arrays.array(
                new MemberResponseDTO("fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                         "alice@mail.com", "alice","password",
                        LocalDateTime.of(2024, 12, 25, 12, 30, 10),
                        LocalDateTime.of(2024, 12, 25, 12, 30, 10)),
                new MemberResponseDTO("da71b2f7-2a51-4601-9ecb-cf9fd4aea40b",
                        "bob@mail.com", "bob","1234",
                        LocalDateTime.of(2024, 12, 25, 12, 30, 10),
                        LocalDateTime.of(2024, 12, 25, 12, 30, 10)),
                new MemberResponseDTO("eeceac6c-c64b-4cb1-a922-bcae509a8998",
                        "john@mail.com","john","5678",
                        LocalDateTime.of(2024, 12, 25, 12, 30, 10),
                        LocalDateTime.of(2024, 12, 25, 12, 30, 10))
        );
    }

    @Test
    @DisplayName("List 역직렬화 테스트: json -> 리스트")
    void memberListDeserializationTest() throws IOException {
        String expected = """
                [
                  {
                    "memberId": "fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                    "email": "alice@mail.com",
                    "username": "alice",
                    "password": "password",
                    "createdAt": "2024-12-25T12:30:10",
                    "updatedAt": "2024-12-25T12:30:10"
                  },
                  {
                    "memberId": "da71b2f7-2a51-4601-9ecb-cf9fd4aea40b",
                    "email": "bob@mail.com",
                    "username": "bob",
                    "password": "1234",
                    "createdAt": "2024-12-25T12:30:10",
                    "updatedAt": "2024-12-25T12:30:10"
                  },
                  {
                    "memberId": "eeceac6c-c64b-4cb1-a922-bcae509a8998",
                    "email": "john@mail.com",
                    "username": "john",
                    "password": "5678",
                    "createdAt": "2024-12-25T12:30:10",
                    "updatedAt": "2024-12-25T12:30:10"
                  }
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(responseDTOS);
    }

    @Test
    @DisplayName("List 직렬화 테스트: MemberResponseDTOList -> json")
    void memberListSerializationTest() throws IOException {
        assertThat(jsonList.write(responseDTOS)).isStrictlyEqualToJson("memberResponseDTOList.json");
    }

    @Test
    @DisplayName("객체 역직렬화 테스트: json -> MemberResponseDTO")
    void memberResponseDTO_deserialization_test() throws IOException{
        //  given
        String expected = """
                {
                    "memberId": "fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                    "email": "alice@mail.com",
                    "username": "alice",
                    "password": "password",
                    "createdAt": "2024-12-25T12:30:10",
                    "updatedAt": "2024-12-25T12:30:10"
                }
                """;

        //  when: json -> 객체
        ObjectContent<MemberResponseDTO> dtoObjectContent = json.parse(expected);

        //  then
        assertThat(dtoObjectContent).isEqualTo(new MemberResponseDTO(
                "fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                "alice@mail.com", "alice","password",
                LocalDateTime.of(2024, 12, 25, 12, 30, 10),
                LocalDateTime.of(2024, 12, 25, 12, 30, 10)));
        assertThat(json.parseObject(expected).memberId()).isEqualTo("fc7b9203-f569-46d2-8bf0-9e23f2a131bf");
        assertThat(json.parseObject(expected).username()).isEqualTo("alice");
    }

    @Test
    @DisplayName("객체 직렬화 테스트: MemberResponseDTO -> json")
    void memberResponseDTO_serialization_test() throws IOException {
        //  given
        MemberResponseDTO member = new MemberResponseDTO(
                "fc7b9203-f569-46d2-8bf0-9e23f2a131bf",
                 "alice@mail.com","alice", "password",
                LocalDateTime.of(2024, 12, 25, 12, 30, 10),
                LocalDateTime.of(2024, 12, 25, 12, 30, 10));

        //  when: MemberResponseDTO 를 json 으로 변환
        JsonContent<MemberResponseDTO> dtoJsonContent = json.write(member);

        //  then: 미리 작성된 예상 json 파일과 같은 지 확인, 필드 값 확인
        assertThat(dtoJsonContent).isStrictlyEqualToJson("memberResponseDTOExpected.json");
        assertThat(dtoJsonContent).hasJsonPathStringValue("@.memberId");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("@.memberId")
                .isEqualTo("fc7b9203-f569-46d2-8bf0-9e23f2a131bf");
        assertThat(dtoJsonContent).hasJsonPathStringValue("@.email");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("@.email")
                .isEqualTo("alice@mail.com");
        assertThat(dtoJsonContent).hasJsonPathStringValue("@.username");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("@.username")
                .isEqualTo("alice");
        assertThat(dtoJsonContent).hasJsonPathStringValue("@.password");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("@.password")
                .isEqualTo("password");
        assertThat(dtoJsonContent).hasJsonPathStringValue("@.createdAt");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("@.createdAt")
                .isEqualTo("2024-12-25T12:30:10");
    }

}