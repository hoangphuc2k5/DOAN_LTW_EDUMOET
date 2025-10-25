package com.stackoverflow.repository;

import com.stackoverflow.model.Answer;
import com.stackoverflow.model.Comment;
import com.stackoverflow.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByQuestionOrderByCreatedAtAsc(Question question);
    
    List<Comment> findByAnswerOrderByCreatedAtAsc(Answer answer);

    // Admin features
    Page<Comment> findByBodyContaining(String keyword, Pageable pageable);
    
    Page<Comment> findByIsApproved(Boolean isApproved, Pageable pageable);
    
    Page<Comment> findByIsDeleted(Boolean isDeleted, Pageable pageable);
    
    long countByIsApproved(Boolean isApproved);
}

