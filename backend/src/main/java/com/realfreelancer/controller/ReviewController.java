package com.realfreelancer.controller;

import com.realfreelancer.model.Review;
import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.ReviewRepository;
import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.service.ReviewService;
import com.realfreelancer.dto.ReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a review
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Project> projectOpt = projectRepository.findById(reviewRequest.getProjectId());
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            
            // Check if user can review this project
            if (!project.getClient().getId().equals(reviewer.getId()) && 
                !project.getFreelancer().getId().equals(reviewer.getId())) {
                return ResponseEntity.status(403).body("You can only review projects you participated in");
            }

            // Check if project is completed
            if (project.getStatus() != Project.ProjectStatus.COMPLETED) {
                return ResponseEntity.badRequest().body("You can only review completed projects");
            }

            // Check if already reviewed
            if (reviewRepository.existsByProjectAndReviewer(project, reviewer)) {
                return ResponseEntity.badRequest().body("You have already reviewed this project");
            }

            Review review = new Review();
            review.setRating(reviewRequest.getRating());
            review.setComment(reviewRequest.getComment());
            review.setProject(project);
            review.setReviewer(reviewer);
            review.setReviewedUser(reviewRequest.getReviewedUserId() != null ? 
                userRepository.findById(reviewRequest.getReviewedUserId()).orElse(null) : null);

            Review savedReview = reviewService.createReview(review);
            return ResponseEntity.ok(savedReview);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating review: " + e.getMessage());
        }
    }

    // Get reviews for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Review> reviews = reviewRepository.findByReviewedUser(user, pageable);
            
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reviews: " + e.getMessage());
        }
    }

    // Get reviews for a project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getProjectReviews(@PathVariable Long projectId) {
        try {
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            List<Review> reviews = reviewRepository.findByProject(project);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching project reviews: " + e.getMessage());
        }
    }

    // Get user's average rating
    @GetMapping("/user/{userId}/rating")
    public ResponseEntity<?> getUserAverageRating(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            Double averageRating = reviewRepository.findAverageRatingByReviewedUser(user);
            Long totalReviews = reviewRepository.countByReviewedUser(user);

            return ResponseEntity.ok(Map.of(
                "averageRating", averageRating != null ? averageRating : 0.0,
                "totalReviews", totalReviews
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user rating: " + e.getMessage());
        }
    }

    // Get user's badges
    @GetMapping("/user/{userId}/badges")
    public ResponseEntity<?> getUserBadges(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            List<Badge> badges = reviewService.getUserBadges(user);
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user badges: " + e.getMessage());
        }
    }

    // Update a review
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
            if (!reviewOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Review review = reviewOpt.get();
            if (!review.getReviewer().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only update your own reviews");
            }

            review.setRating(reviewRequest.getRating());
            review.setComment(reviewRequest.getComment());

            Review updatedReview = reviewService.updateReview(review);
            return ResponseEntity.ok(updatedReview);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating review: " + e.getMessage());
        }
    }

    // Delete a review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
            if (!reviewOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Review review = reviewOpt.get();
            if (!review.getReviewer().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only delete your own reviews");
            }

            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("Review deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting review: " + e.getMessage());
        }
    }

    // Get top rated users
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedUsers(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<User> topUsers = reviewService.getTopRatedUsers(limit);
            return ResponseEntity.ok(topUsers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching top rated users: " + e.getMessage());
        }
    }
} 