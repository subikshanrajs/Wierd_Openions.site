package com.yourcompany.weirdopinions.service;

import com.yourcompany.weirdopinions.model.*;
import com.yourcompany.weirdopinions.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private VoteRepository voteRepository;

    public List<Comment> getCommentsByQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return commentRepository.findByQuestionOrderByCreatedAtAsc(question);
    }

    public List<Comment> getRepliesByComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentRepository.findByParentCommentOrderByCreatedAtAsc(comment);
    }

    public Comment createComment(String content, Long questionId, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        Comment comment = new Comment(content, user, question);
        return commentRepository.save(comment);
    }

    public Comment createReply(String content, Long parentCommentId, User user) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        
        Comment reply = new Comment(content, user, parentComment.getQuestion());
        reply.setParentComment(parentComment);
        return commentRepository.save(reply);
    }

    public Comment updateComment(Long commentId, String content, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this comment");
        }
        
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        if (!comment.getUser().getId().equals(user.getId()) && 
            !user.getRoles().contains(RoleName.ROLE_ADMIN)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }
        
        commentRepository.delete(comment);
    }

    public Vote voteOnComment(Long commentId, VoteType voteType, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Check if user already voted
        Optional<Vote> existingVote = voteRepository.findByUserAndComment(user, comment);
        
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
            Vote vote = new Vote(voteType, user, comment);
            return voteRepository.save(vote);
        }
    }

    public Page<Comment> getUserComments(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }
}