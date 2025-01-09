package lime1st.limeApp.blog.application.dto;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BlogDTOJsonTest {

    @Autowired
    private JacksonTester<BlogDTO> json;

    @Autowired
    private JacksonTester<BlogDTO[]> jsonList;

    private BlogDTO[] blogDtoArr;

    @BeforeEach
    void setUp() {
        blogDtoArr = Arrays.array(
                new BlogDTO(11L, "test title alice1", "test content alice1", "alice",
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00),
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00)),
                new BlogDTO(12L, "test title alice2", "test content alice2", "alice",
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00),
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00)),
                new BlogDTO(13L, "test title alice3", "test content alice3", "alice",
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00),
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00)),
                new BlogDTO(14L, "test title bob1", "test content bob1", "bob",
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00),
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00)),
                new BlogDTO(15L, "test title bob2", "test content bob2", "bob",
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00),
                        LocalDateTime.of(2024, 12, 10, 17, 40, 00))
        );
    }

    @Test
    @DisplayName("BlogDTO 역직렬화 테스트 json -> List")
    void BlogDTO_list_deserialization_test() throws IOException {
        String expected = """
                [
                   {
                     "id": 11,
                     "title": "test title alice1",
                     "content": "test content alice1",
                     "author": "alice",
                     "createdAt": "2024-12-10T17:40:00",
                     "updatedAt": "2024-12-10T17:40:00"
                   },
                   {
                     "id": 12,
                     "title": "test title alice2",
                     "content": "test content alice2",
                     "author": "alice",
                     "createdAt": "2024-12-10T17:40:00",
                     "updatedAt": "2024-12-10T17:40:00"
                   },
                   {
                     "id": 13,
                     "title": "test title alice3",
                     "content": "test content alice3",
                     "author": "alice",
                     "createdAt": "2024-12-10T17:40:00",
                     "updatedAt": "2024-12-10T17:40:00"
                   },
                   {
                     "id": 14,
                     "title": "test title bob1",
                     "content": "test content bob1",
                     "author": "bob",
                     "createdAt": "2024-12-10T17:40:00",
                     "updatedAt": "2024-12-10T17:40:00"
                   },
                   {
                     "id": 15,
                     "title": "test title bob2",
                     "content": "test content bob2",
                     "author": "bob",
                     "createdAt": "2024-12-10T17:40:00",
                     "updatedAt": "2024-12-10T17:40:00"
                   }
                 ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(blogDtoArr);
    }

    @Test
    @DisplayName("BlogDTO List 직렬화 테스트 List -> json")
    void BlogDTO_list_serialization_test() throws IOException {
        assertThat(jsonList.write(blogDtoArr)).isStrictlyEqualToJson("blogDTOList.json");
    }

    @Test
    @DisplayName("객체 역직렬화 테스트 json -> BlogDTO")
    void blogDTO_deserialization_test() throws IOException {
        String expected = """
                {
                    "id": 11,
                    "title": "test title",
                    "content": "test content",
                    "author": "tester",
                    "createdAt": "2020-12-12T11:11:11",
                    "updatedAt": "2020-12-12T11:11:11"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new BlogDTO(11L, "test title",
                "test content", "tester",
                LocalDateTime.of(2020, 12, 12, 11, 11, 11),
                LocalDateTime.of(2020, 12, 12, 11, 11, 11)));
        assertThat(json.parseObject(expected).id()).isEqualTo(11);
        assertThat(json.parseObject(expected).title()).isEqualTo("test title");
        assertThat(json.parseObject(expected).content()).isEqualTo("test content");
        assertThat(json.parseObject(expected).author()).isEqualTo("tester");
    }

    @Test
    @DisplayName("객체 직렬화 테스트: BlogDTO -> json")
    void blogDTO_serialization_test() throws IOException {
        BlogDTO blogDTO = new BlogDTO(100L, "test title", "test content","tester",
                LocalDateTime.of(2024, 12, 10, 17, 40, 00),
                LocalDateTime.of(2024, 12, 10, 17, 40, 00));

        assertThat(json.write(blogDTO)).isStrictlyEqualToJson("blogDTOExpected.json");
        assertThat(json.write(blogDTO)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(blogDTO)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(100);
        assertThat(json.write(blogDTO)).hasJsonPathStringValue("@.title");
        assertThat(json.write(blogDTO)).extractingJsonPathStringValue("@.title")
                .isEqualTo("test title");
        assertThat(json.write(blogDTO)).hasJsonPathStringValue("@.content");
        assertThat(json.write(blogDTO)).extractingJsonPathStringValue("@.content")
                .isEqualTo("test content");
        assertThat(json.write(blogDTO)).hasJsonPathStringValue("@.author");
        assertThat(json.write(blogDTO)).extractingJsonPathStringValue("@.author")
                .isEqualTo("tester");
    }
}