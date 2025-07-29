package com.poortorich.tag.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.tag.constants.TagValidationConstraints;
import com.poortorich.tag.entity.Tag;
import com.poortorich.tag.repository.TagRepository;
import com.poortorich.tag.response.enums.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Captor
    private ArgumentCaptor<List<Tag>> tagsCaptor;

    private List<String> tagNames;
    private Chatroom chatroom;
    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private List<Tag> tags;

    @BeforeEach
    void setUp() {
        tagNames = List.of("태그1", "태그2", "태그3");
        chatroom = Chatroom.builder().build();
        tag1 = Tag.builder().name("태그1").chatroom(chatroom).build();
        tag2 = Tag.builder().name("태그2").chatroom(chatroom).build();
        tag3 = Tag.builder().name("태그3").chatroom(chatroom).build();
        tags = List.of(tag1, tag2, tag3);
    }

    @Test
    @DisplayName("태그 리스트 저장 성공")
    void createTagSuccess() {
        tagService.createTag(tagNames, chatroom);

        verify(tagRepository).saveAll(tagsCaptor.capture());
        List<Tag> savedTags = tagsCaptor.getValue();

        assertThat(savedTags.size()).isEqualTo(3);
        assertThat(savedTags.get(0).getName()).isEqualTo(tags.get(0).getName());
        assertThat(savedTags.get(1).getName()).isEqualTo(tags.get(1).getName());
        assertThat(savedTags.get(2).getName()).isEqualTo(tags.get(2).getName());
    }

    @Test
    @DisplayName("태그 이름이 최대 길이가 넘어가는 경우 예외 발생")
    void createTagNameTooLong() {
        String tooLongName = "A".repeat(TagValidationConstraints.TAG_NAME_MAX + 1);
        List<String> tagNames = List.of(tooLongName);
        Chatroom chatroom = Chatroom.builder().build();

        assertThatThrownBy(() -> tagService.createTag(tagNames, chatroom))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(TagResponse.TAG_NAME_TOO_LONG.getMessage());
    }

    @Test
    @DisplayName("태그 이름 리스트 조회 성공")
    void getTagNamesSuccess() {
        when(tagRepository.findByChatroom(chatroom)).thenReturn(tags);

        List<String> result = tagService.getTagNames(chatroom);

        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).isEqualTo(tagNames.get(0));
        assertThat(result.get(1)).isEqualTo(tagNames.get(1));
        assertThat(result.get(2)).isEqualTo(tagNames.get(2));
    }

    @Test
    @DisplayName("태그 이름 리스트가 없는 경우 빈 리스트 반환")
    void getTagNamesEmptyList() {
        when(tagRepository.findByChatroom(chatroom)).thenReturn(List.of());

        List<String> result = tagService.getTagNames(chatroom);

        assertThat(result.isEmpty()).isTrue();
    }
}
