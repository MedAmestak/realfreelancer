package com.realfreelancer.repository;

import com.realfreelancer.model.Badge;
import com.realfreelancer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    List<Badge> findByUser(User user);
    
    List<Badge> findByUserOrderByEarnedAtDesc(User user);
    
    @Query("SELECT b FROM Badge b WHERE b.user = :user AND b.type = :type")
    List<Badge> findByUserAndType(@Param("user") User user, @Param("type") Badge.BadgeType type);
    
    @Query("SELECT b FROM Badge b WHERE b.user = :user AND b.isRare = true")
    List<Badge> findRareBadgesByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(b) FROM Badge b WHERE b.user = :user")
    Long countBadgesByUser(@Param("user") User user);
    
    @Query("SELECT b FROM Badge b WHERE b.name = :name AND b.user = :user")
    Badge findByNameAndUser(@Param("name") String name, @Param("user") User user);
    
    @Query("SELECT b FROM Badge b WHERE b.user = :user ORDER BY b.earnedAt DESC LIMIT 1")
    Badge findLatestBadgeByUser(@Param("user") User user);
    
    @Query("SELECT b FROM Badge b WHERE b.type = 'REPUTATION' AND b.user = :user ORDER BY b.earnedAt DESC")
    List<Badge> findReputationBadgesByUser(@Param("user") User user);
    
    @Query("SELECT b FROM Badge b WHERE b.type = 'PROJECTS' AND b.user = :user ORDER BY b.earnedAt DESC")
    List<Badge> findProjectBadgesByUser(@Param("user") User user);
    
    @Query("SELECT b FROM Badge b WHERE b.type = 'SKILLS' AND b.user = :user ORDER BY b.earnedAt DESC")
    List<Badge> findSkillBadgesByUser(@Param("user") User user);
} 