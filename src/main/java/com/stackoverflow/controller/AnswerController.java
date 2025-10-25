package com.stackoverflow.controller;

import com.stackoverflow.model.Answer;
import com.stackoverflow.model.Question;
import com.stackoverflow.model.User;
import com.stackoverflow.service.AnswerService;
import com.stackoverflow.service.QuestionService;
import com.stackoverflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @PostMapping("/question/{questionId}")
    public String postAnswer(
            @PathVariable Long questionId,
            @Valid @ModelAttribute Answer answer,
            BindingResult result,
            Authentication authentication,
            Model model) {
        
        if (result.hasErrors()) {
            return "redirect:/questions/" + questionId;
        }
        
        Question question = questionService.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        User author = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        answer.setQuestion(question);
        answer.setAuthor(author);
        answerService.createAnswer(answer);
        
        return "redirect:/questions/" + questionId;
    }

    @GetMapping("/{id}/edit")
    public String editAnswerForm(@PathVariable Long id, Model model, Authentication authentication) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!answer.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + answer.getQuestion().getId();
        }
        
        model.addAttribute("answer", answer);
        model.addAttribute("pageTitle", "Edit Answer - Stack Overflow Clone");
        return "answer/edit";
    }

    @PostMapping("/{id}/edit")
    public String editAnswer(
            @PathVariable Long id,
            @Valid @ModelAttribute Answer updatedAnswer,
            BindingResult result,
            Authentication authentication) {
        
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!answer.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + answer.getQuestion().getId();
        }
        
        answer.setBody(updatedAnswer.getBody());
        answerService.updateAnswer(answer);
        
        return "redirect:/questions/" + answer.getQuestion().getId();
    }

    @PostMapping("/{id}/delete")
    public String deleteAnswer(@PathVariable Long id, Authentication authentication) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long questionId = answer.getQuestion().getId();
        
        if (!answer.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + questionId;
        }
        
        answerService.deleteAnswer(id);
        return "redirect:/questions/" + questionId;
    }

    @PostMapping("/{id}/accept")
    public String acceptAnswer(@PathVariable Long id, Authentication authentication) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only question author can accept answers
        if (!answer.getQuestion().getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/questions/" + answer.getQuestion().getId();
        }
        
        answerService.acceptAnswer(answer);
        return "redirect:/questions/" + answer.getQuestion().getId();
    }

    @PostMapping("/{id}/upvote")
    @ResponseBody
    public String upvoteAnswer(@PathVariable Long id, Authentication authentication) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        answerService.upvoteAnswer(answer, user);
        return String.valueOf(answer.getVotes());
    }

    @PostMapping("/{id}/downvote")
    @ResponseBody
    public String downvoteAnswer(@PathVariable Long id, Authentication authentication) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        answerService.downvoteAnswer(answer, user);
        return String.valueOf(answer.getVotes());
    }
}

