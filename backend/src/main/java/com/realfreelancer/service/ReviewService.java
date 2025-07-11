package com.realfreelancer.service;

import com.realfreelancer.model.Review;
import com.realfreelancer.model.User;
import com.realfreelancer.model.Badge;
import com.realfreelancer.repository.ReviewRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    public Review createReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        
        if (review.getReviewedUser() != null) {
            checkAndAssignBadges(review.getReviewedUser());
        }
        
        return savedReview;
    }

    public Review updateReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public List<Badge> getUserBadges(User user) {
        return badgeRepository.findByUser(user);
    }

    public List<User> getTopRatedUsers(int limit) {
        return reviewRepository.findTopRatedUsers(limit);
    }

    private void checkAndAssignBadges(User user) {
        Long totalReviews = reviewRepository.countByReviewedUser(user);
        Double averageRating = reviewRepository.findAverageRatingByReviewedUser(user);
        
        if (averageRating == null) {
            averageRating = 0.0;
        }

        List<Badge> existingBadges = badgeRepository.findByUser(user);
        List<String> existingBadgeTypes = existingBadges.stream()
            .map(Badge::getType)
            .map(Badge.BadgeType::name)
            .toList();

        // First Review Badge
        if (totalReviews >= 1 && !existingBadgeTypes.contains("REPUTATION")) {
            assignBadge(user, Badge.BadgeType.REPUTATION, "First Review", "Completed your first review");
        }

        // 10 Reviews Badge
        if (totalReviews >= 10 && !existingBadgeTypes.contains("PROJECTS")) {
            assignBadge(user, Badge.BadgeType.PROJECTS, "Review Milestone", "Completed 10 reviews");
        }

        // 50 Reviews Badge
        if (totalReviews >= 50 && !existingBadgeTypes.contains("SKILLS")) {
            assignBadge(user, Badge.BadgeType.SKILLS, "Review Expert", "Completed 50 reviews");
        }

        // High Rating Badge (4.5+ average)
        if (averageRating >= 4.5 && totalReviews >= 5 && !existingBadgeTypes.contains("SPECIAL")) {
            assignBadge(user, Badge.BadgeType.SPECIAL, "High Rating", "Maintained 4.5+ average rating");
        }

        // Perfect Rating Badge (5.0 average)
        if (averageRating == 5.0 && totalReviews >= 3) {
            // Check if user already has a perfect rating badge
            boolean hasPerfectBadge = existingBadges.stream()
                .anyMatch(badge -> badge.getName().equals("Perfect Rating"));
            if (!hasPerfectBadge) {
                assignBadge(user, Badge.BadgeType.SPECIAL, "Perfect Rating", "Achieved perfect 5.0 rating");
            }
        }

        // Consistent Reviewer Badge (10+ reviews in 30 days)
        if (totalReviews >= 10) {
            Long recentReviews = reviewRepository.countRecentReviewsByUser(user, 30);
            if (recentReviews >= 10) {
                boolean hasConsistentBadge = existingBadges.stream()
                    .anyMatch(badge -> badge.getName().equals("Consistent Reviewer"));
                if (!hasConsistentBadge) {
                    assignBadge(user, Badge.BadgeType.REPUTATION, "Consistent Reviewer", "Active reviewer");
                }
            }
        }
    }

    private void assignBadge(User user, Badge.BadgeType type, String name, String description) {
        Badge badge = new Badge();
        badge.setUser(user);
        badge.setType(type);
        badge.setName(name);
        badge.setDescription(description);
        badgeRepository.save(badge);
    }
} 