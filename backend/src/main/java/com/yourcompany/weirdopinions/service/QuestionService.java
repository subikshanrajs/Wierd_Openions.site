package com.yourcompany.weirdopinions.service;

import com.yourcompany.weirdopinions.model.*;
import com.yourcompany.weirdopinions.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollVoteRepository pollVoteRepository;

    @Autowired
    private UserService userService;

    public Page<Question> getAllQuestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Question> getQuestionsByCategory(Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findByCategory(category, pageable);
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question createQuestion(String title, String description, Category category, 
                                 List<String> pollOptions, String imageUrl, User user) {
        Question question = new Question(title, description, category, user);
        question.setImageUrl(imageUrl);
        
        if (pollOptions != null && !pollOptions.isEmpty()) {
            question.setPollOptions(pollOptions);
            question.setHasPoll(true);
        }
        
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long questionId, String title, String description, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        if (!question.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this question");
        }
        
        question.setTitle(title);
        question.setDescription(description);
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        if (!question.getUser().getId().equals(user.getId()) && 
            !user.getRoles().contains(RoleName.ROLE_ADMIN)) {
            throw new RuntimeException("Not authorized to delete this question");
        }
        
        questionRepository.delete(question);
    }

    public Vote voteOnQuestion(Long questionId, VoteType voteType, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Check if user already voted
        Optional<Vote> existingVote = voteRepository.findByUserAndQuestion(user, question);
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // Remove vote if clicking same vote type
                voteRepository.delete(vote);
                return null;
            } else {
                // Update vote type
                vote.setVoteType(voteType);
                return voteRepository.save(vote);
            }
        } else {
            // Create new vote
            Vote vote = new Vote(voteType, user, question);
            return voteRepository.save(vote);
        }
    }

    public PollVote voteOnPoll(Long questionId, int optionIndex, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        if (!question.isHasPoll()) {
            throw new RuntimeException("Question does not have a poll");
        }
        
        if (optionIndex < 0 || optionIndex >= question.getPollOptions().size()) {
            throw new RuntimeException("Invalid poll option");
        }
        
        // Check if user already voted on poll
        Optional<PollVote> existingVote = pollVoteRepository.findByUserAndQuestion(user, question);
        
        if (existingVote.isPresent()) {
            PollVote vote = existingVote.get();
            vote.setOptionIndex(optionIndex);
            return pollVoteRepository.save(vote);
        } else {
            PollVote vote = new PollVote(optionIndex, user, question);
            return pollVoteRepository.save(vote);
        }
    }

    public List<Question> getTrendingQuestions() {
        Instant since = Instant.now().minus(7, ChronoUnit.DAYS);
        return questionRepository.findTrendingQuestions(since, PageRequest.of(0, 10));
    }

    public Page<Question> searchQuestions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.searchQuestions(keyword, pageable);
    }

    public Page<Question> getUserQuestions(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }