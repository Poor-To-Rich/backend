package com.poortorich.tag.service;

import com.poortorich.chat.entity.Chatroom;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.tag.constants.TagValidationConstraints;
import com.poortorich.tag.entity.Tag;
import com.poortorich.tag.repository.TagRepository;
import com.poortorich.tag.response.enums.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public void createTag(List<String> hashtags, Chatroom chatroom) {
        List<Tag> tag = hashtags.stream()
                .map(tagName -> buildTag(tagName, chatroom))
                .toList();

        tagRepository.saveAll(tag);
    }

    private Tag buildTag(String tagName, Chatroom chatroom) {
        return Tag.builder()
                .name(validateTagName(tagName))
                .chatroom(chatroom)
                .build();
    }

    private String validateTagName(String tagName) {
        if (tagName.length() > TagValidationConstraints.TAG_NAME_MAX) {
            throw new BadRequestException(TagResponse.TAG_NAME_TOO_LONG);
        }

        return tagName;
    }
}
