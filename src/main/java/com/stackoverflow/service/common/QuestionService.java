package com.stackoverflow.service.common;

import com.stackoverflow.entity.Question;
import com.stackoverflow.entity.Tag;
import com.stackoverflow.entity.User;
import com.stackoverflow.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TagService tagService;

    public Question createQuestion(Question question, Set<String> tagNames) {
        Set<Tag> tags = tagService.getOrCreateTags(tagNames);
        question.setTags(tags);
        question.setViews(0);
        question.setVotes(0);
        
        Question savedQuestion = questionRepository.save(question);
        
        // Increment question count for each tag
        tags.forEach(Tag::incrementQuestionCount);
        
        return savedQuestion;
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findByIdWithTagsAndAuthor(id);
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findByIdWithTagsAndAuthor(id)
            .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public Page<Question> getAllQuestions(Pageable pageable) {
        // Use findAll() instead of findAllByOrderByCreatedAtDesc to avoid duplicate ORDER BY
        // when Pageable already contains Sort
        return questionRepository.findAll(pageable);
    }

    public Page<Question> getQuestionsByVotes(Pageable pageable) {
        // Use custom query to avoid duplicate ORDER BY when Pageable contains Sort
        return questionRepository.findAllByOrderByVotesDesc(pageable);
    }

    public Page<Question> getQuestionsByAuthor(User author, Pageable pageable) {
        return questionRepository.findByAuthor(author, pageable);
    }

    public Page<Question> getQuestionsByTag(Tag tag, Pageable pageable) {
        return questionRepository.findByTag(tag, pageable);
    }

    public Page<Question> searchQuestions(String search, Pageable pageable) {
        return questionRepository.searchQuestions(search, pageable);
    }

    public Question updateQuestion(Question question, Set<String> tagNames) {
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(tagNames);
            question.setTags(tags);
        }
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Decrement question count for each tag
        question.getTags().forEach(Tag::decrementQuestionCount);
        
        questionRepository.deleteById(id);
    }

    public void incrementViews(Question question) {
        question.incrementViews();
        questionRepository.save(question);
    }

    public void upvoteQuestion(Question question, User user) {
        if (!user.getVotedQuestions().contains(question)) {
            question.upvote();
            user.getVotedQuestions().add(question);
            questionRepository.save(question);
        }
    }

    public void downvoteQuestion(Question question, User user) {
        if (user.getVotedQuestions().contains(question)) {
            question.downvote();
            user.getVotedQuestions().remove(question);
            questionRepository.save(question);
        }
    }

    public Long countByAuthor(User author) {
        return questionRepository.countByAuthor(author);
    }

    // ================== ADMIN FEATURES ==================
    
    public void pinQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsPinned(true);
        questionRepository.save(question);
    }

    public void unpinQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsPinned(false);
        questionRepository.save(question);
    }

    public void lockQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsLocked(true);
        questionRepository.save(question);
    }

    public void unlockQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsLocked(false);
        questionRepository.save(question);
    }

    public void approveQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsApproved(true);
        questionRepository.save(question);
    }

    public void rejectQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsApproved(false);
        questionRepository.save(question);
    }

    @Transactional
    public Question save(Question question) {
        return questionRepository.save(question);
    }
}