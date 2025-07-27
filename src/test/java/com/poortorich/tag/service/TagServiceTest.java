package com.poortorich.tag.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.tag.constants.TagValidationConstraints;
import com.poortorich.tag.entity.Tag;
import com.poortorich.tag.repository.TagRepository;
import com.poortorich.tag.response.enums.TagResponse;
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

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Captor
    private ArgumentCaptor<List<Tag>> tagsCaptor;

    @Test
    @DisplayName("태그 리스트 저장 성공")
    void createTagSuccess() {
        List<String> tagNames = List.of("태그1", "태그2", "태그3");
        Chatroom chatroom = Chatroom.builder().build();
        Tag tag1 = Tag.builder().name("태그1").chatroom(chatroom).build();
        Tag tag2 = Tag.builder().name("태그2").chatroom(chatroom).build();
        Tag tag3 = Tag.builder().name("태그3").chatroom(chatroom).build();
        List<Tag> tags = List.of(tag1, tag2, tag3);

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
}
