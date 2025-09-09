package com.yl.musinsa2.controller;

import com.yl.musinsa2.client.PostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OpenFeign 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    private JsonPlaceholderClient jsonPlaceholderClient;

    @Test
    void testGetPost() {
        // Given
        Long postId = 1L;

        // When
        PostResponse response = jsonPlaceholderClient.getPost(postId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(postId);
        assertThat(response.getTitle()).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getUserId()).isNotNull();
    }

    @Test
    void testGetAllPosts() {
        // When
        PostResponse[] responses = jsonPlaceholderClient.getAllPosts();

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses.length).isGreaterThan(0);
        assertThat(responses[0].getId()).isNotNull();
        assertThat(responses[0].getTitle()).isNotNull();
    }
}
