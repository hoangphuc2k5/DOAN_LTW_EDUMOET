package com.stackoverflow.repository;

import com.stackoverflow.model.ImageAttachment;
import com.stackoverflow.model.Question;
import com.stackoverflow.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageAttachmentRepository extends JpaRepository<ImageAttachment, Long> {
    List<ImageAttachment> findByQuestion(Question question);
    List<ImageAttachment> findByAnswer(Answer answer);
}