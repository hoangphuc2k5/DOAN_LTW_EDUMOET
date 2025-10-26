package com.stackoverflow.service;

import com.stackoverflow.model.Tag;
import com.stackoverflow.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Tag createTag(Tag tag) {
        tag.setQuestionCount(0);
        return tagRepository.save(tag);
    }

    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public Page<Tag> getTagsByPopularity(Pageable pageable) {
        return tagRepository.findAllByOrderByQuestionCountDesc(pageable);
    }

    public Page<Tag> getTagsByName(Pageable pageable) {
        return tagRepository.findAllByOrderByNameAsc(pageable);
    }

    public Page<Tag> searchTags(String search, Pageable pageable) {
        return tagRepository.searchTags(search, pageable);
    }

    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            // Normalize tag name by trimming and converting to lowercase
            String normalizedName = name.trim().toLowerCase();
            if (!normalizedName.isEmpty()) {
                Tag tag = tagRepository.findByName(normalizedName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(normalizedName);
                        newTag.setQuestionCount(0);
                        return tagRepository.save(newTag);
                    });
                tags.add(tag);
            }
        }
        return tags;
    }

    public Tag updateTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }
}