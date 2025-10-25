package com.stackoverflow.controller;

import com.stackoverflow.model.Question;
import com.stackoverflow.model.User;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.CommentService;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Set;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Increment views
        questionService.incrementViews(question);
        
        model.addAttribute("question", question);
        model.addAttribute("answers", answerService.getAnswersByQuestion(question));
        model.addAttribute("questionComments", commentService.getCommentsByQuestion(question));
        model.addAttribute("pageTitle", question.getTitle() + " - Stack Overflow Clone");
        
        return "question/view";
    }

    @GetMapping("/ask")
    public String askQuestionForm(Model model) {
        model.addAttribute("question", new Question());
        model.addAttribute("pageTitle", "Ask a Question - Stack Overflow Clone");
        return "question/ask";
    }

    @PostMapping("/ask")
    public String askQuestion(
            @Valid @ModelAttribute Question question,
            @RequestParam String tags,
            BindingResult result,
            Authentication authentication,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Ask a Question - Stack Overflow Clone");
            return "question/ask";
        }
        
        User author = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        question.setAuthor(author);
        
        // Parse tags
        Set<String> tagNames = Set.of(tags.split("\\s+"));
        Question savedQuestion = questionService.createQuestion(question, tagNames);
        
        return "redirect:/questions/" + savedQuestion.getId();
    }

    @GetMapping("/{id}/edit")
    public String editQuestionForm(@PathVariable Long id, Model model, Authentication authentication) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!question.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + id;
        }
        
        model.addAttribute("question", question);
        model.addAttribute("pageTitle", "Edit Question - Stack Overflow Clone");
        return "question/edit";
    }

    @PostMapping("/{id}/edit")
    public String editQuestion(
            @PathVariable Long id,
            @Valid @ModelAttribute Question updatedQuestion,
            @RequestParam(required = false) String tags,
            BindingResult result,
            Authentication authentication,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Question - Stack Overflow Clone");
            return "question/edit";
        }
        
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!question.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + id;
        }
        
        question.setTitle(updatedQuestion.getTitle());
        question.setBody(updatedQuestion.getBody());
        
        if (tags != null && !tags.isEmpty()) {
            Set<String> tagNames = Set.of(tags.split("\\s+"));
            questionService.updateQuestion(question, tagNames);
        } else {
            questionService.updateQuestion(question, null);
        }
        
        return "redirect:/questions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, Authentication authentication) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!question.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + id;
        }
        
        questionService.deleteQuestion(id);
        return "redirect:/";
    }

    @PostMapping("/{id}/upvote")
    @ResponseBody
    public String upvoteQuestion(@PathVariable Long id, Authentication authentication) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        questionService.upvoteQuestion(question, user);
        return String.valueOf(question.getVotes());
    }

    @PostMapping("/{id}/downvote")
    @ResponseBody
    public String downvoteQuestion(@PathVariable Long id, Authentication authentication) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        questionService.downvoteQuestion(question, user);
        return String.valueOf(question.getVotes());
    }
}

