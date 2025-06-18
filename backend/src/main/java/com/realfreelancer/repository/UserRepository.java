package com.realfreelancer.repository;

import com.realfreelancer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.reputationPoints >= :minPoints ORDER BY u.reputationPoints DESC")
    List<User> findTopUsersByReputation(@Param("minPoints") Integer minPoints);
    
    @Query("SELECT u FROM User u JOIN u.skills s WHERE s IN :skills")
    List<User> findBySkills(@Param("skills") List<String> skills);
    
    @Query("SELECT u FROM User u WHERE u.reputationPoints >= :points")
    List<User> findByReputationPointsGreaterThanEqual(@Param("points") Integer points);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isVerified = true")
    Long countVerifiedUsers();
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
} 