package com.yourcompany.weirdopinions.controller;

import com.yourcompany.weirdopinions.model.*;
import com.yourcompany.weirdopinions.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<Page<Question>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionService.getAllQuestions(page, size));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Question>> getQuestionsByCategory(
            @PathVariable Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(category, page, size));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Question>> getTrendingQuestions() {
        return ResponseEntity.ok(questionService.getTrendingQuestions());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Question>> searchQuestions(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionService.searchQuestions(q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getQuestionComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsByQuestion(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Question> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Question question = questionService.createQuestion(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getPollOptions(),
                request.getImageUrl(),
                user
        );
        
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            Question updatedQuestion = questionService.updateQuestion(
                    id, request.getTitle(), request.getDescription(), user);
            return ResponseEntity.ok(updatedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            questionService.deleteQuestion(id, user);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/vote")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> voteOnQuestion(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            VoteType voteType = VoteType.valueOf(request.get("voteType").toUpperCase());
            Vote vote = questionService.voteOnQuestion(id, voteType, user);
            return ResponseEntity.ok(Map.of("success", true, "vote", vote));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/poll-vote")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> voteOnPoll(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            int optionIndex = request.get("optionIndex");
            PollVote vote = questionService.voteOnPoll(id, optionIndex, user);
            return ResponseEntity.ok(Map.of("success", true, "pollVote", vote));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Request DTOs
    public static class CreateQuestionRequest {
        private String title;
        private String description;
        private Category category;
        private List<String> pollOptions;
        private String imageUrl;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Category getCategory() { return category; }
        public void setCategory(Category category) { this.category = category; }
        public List<String> getPollOptions() { return pollOptions; }
        public void setPollOptions(List<String> pollOptions) { this.pollOptions = pollOptions; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class UpdateQuestionRequest {
        private String title;
        private String description;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}