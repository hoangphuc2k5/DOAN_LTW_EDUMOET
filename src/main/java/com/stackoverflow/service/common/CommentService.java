package com.stackoverflow.service.common;

import com.stackoverflow.entity.Answer;
import com.stackoverflow.entity.Comment;
import com.stackoverflow.entity.Question;
import com.stackoverflow.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getCommentsByQuestion(Question question) {
        return commentRepository.findByQuestionOrderByCreatedAtAsc(question);
    }

    public List<Comment> getCommentsByAnswer(Answer answer) {
        return commentRepository.findByAnswerOrderByCreatedAtAsc(answer);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    // ================== ADMIN FEATURES ==================

    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public Page<Comment> searchComments(String keyword, Pageable pageable) {
        return commentRepository.findByBodyContaining(keyword, pageable);
    }

    public Comment updateComment(Long id, String newBody) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setBody(newBody);
        return commentRepository.save(comment);
    }

    public void softDeleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    public void approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsApproved(true);
        commentRepository.save(comment);
    }

    public void rejectComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsApproved(false);
        commentRepository.save(comment);
    }

    public long countAll() {
        return commentRepository.count();
    }
}

