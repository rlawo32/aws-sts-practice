package com.freelec.prac.springboot.web;

import com.freelec.prac.springboot.domain.posts.Posts;
import com.freelec.prac.springboot.domain.posts.PostsRepository;
import com.freelec.prac.springboot.web.dto.PostsSaveRequestDto;
import com.freelec.prac.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {
        // given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "api/v1/posts";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void Posts_수정된다() throws Exception {
        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        System.out.println("Test 값 확인 title = " + savedPosts.getTitle());
        System.out.println("Test 값 확인 content = " + savedPosts.getContent());
        System.out.println("Test 값 확인 author = " + savedPosts.getAuthor());

        Long updateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        List<Posts> all1 = postsRepository.findAll();

        System.out.println("List Test1 값 확인 title = " + all1.get(0).getTitle());
        System.out.println("List Test1 값 확인 content = " + all1.get(0).getContent());

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.
                exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        System.out.println("Test 값 확인 responseEntity = " + responseEntity.getHeaders());
        System.out.println("Test 값 확인 responseEntity = " + responseEntity.getBody());
        System.out.println("Test 값 확인 responseEntity = " + responseEntity.toString());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);
        List<Posts> all2 = postsRepository.findAll();
        assertThat(all2.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all2.get(0).getContent()).isEqualTo(expectedContent);

        System.out.println("List Test2 값 확인 title = " + all2.get(0).getTitle());
        System.out.println("List Test2 값 확인 content = " + all2.get(0).getContent());
    }
}
