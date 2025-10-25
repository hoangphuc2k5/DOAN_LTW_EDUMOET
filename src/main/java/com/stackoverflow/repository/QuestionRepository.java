package com.stackoverflow.repository;

import com.stackoverflow.model.Question;
import com.stackoverflow.model.Tag;
import com.stackoverflow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.tags LEFT JOIN FETCH q.author ORDER BY q.createdAt DESC")
    List<Question> findAllWithTagsAndAuthor();
    
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.tags LEFT JOIN FETCH q.author WHERE q.id = :id")
    Optional<Question> findByIdWithTagsAndAuthor(@Param("id") Long id);
    
    @Query("SELECT q FROM Question q ORDER BY q.createdAt DESC")
    Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT q FROM Question q ORDER BY q.votes DESC")
    Page<Question> findAllByOrderByVotesDesc(Pageable pageable);
    
    @Query("SELECT q FROM Question q ORDER BY q.answerCount DESC")
    Page<Question> findAllByOrderByAnswersDesc(Pageable pageable);
    
    Page<Question> findByAuthor(User author, Pageable pageable);
    
    Page<Question> findByTagsIn(List<Tag> tags, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(q.body) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Question> searchQuestions(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t = :tag ORDER BY q.createdAt DESC")
    Page<Question> findByTag(@Param("tag") Tag tag, Pageable pageable);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.author = :author")
    Long countByAuthor(@Param("author") User author);

    // Moderation
    Page<Question> findByIsApproved(Boolean isApproved, Pageable pageable);
    
    long countByIsApproved(Boolean isApproved);
}

