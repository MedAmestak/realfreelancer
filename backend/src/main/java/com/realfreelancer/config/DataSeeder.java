package com.realfreelancer.config;

import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if no users exist
        if (userRepository.count() == 0) {
            seedUsers();
            seedProjects();
        }
    }

    private void seedUsers() {
        // Create sample users
        User client1 = new User();
        client1.setUsername("john_client");
        client1.setEmail("john@example.com");
        client1.setPassword(passwordEncoder.encode("password123"));
        client1.setBio("Experienced entrepreneur looking for talented developers");
        client1.setSkills(new HashSet<>(Arrays.asList("Project Management", "Business Strategy")));
        client1.setIsActive(true);
        client1.setIsVerified(true);
        client1.setReputationPoints(100);
        userRepository.save(client1);

        User client2 = new User();
        client2.setUsername("sarah_startup");
        client2.setEmail("sarah@startup.com");
        client2.setPassword(passwordEncoder.encode("password123"));
        client2.setBio("Startup founder building innovative solutions");
        client2.setSkills(new HashSet<>(Arrays.asList("Product Management", "UX Design")));
        client2.setIsActive(true);
        client2.setIsVerified(true);
        client2.setReputationPoints(85);
        userRepository.save(client2);

        User freelancer1 = new User();
        freelancer1.setUsername("alex_dev");
        freelancer1.setEmail("alex@dev.com");
        freelancer1.setPassword(passwordEncoder.encode("password123"));
        freelancer1.setBio("Full-stack developer with 5+ years of experience");
        freelancer1.setSkills(new HashSet<>(Arrays.asList("React", "Node.js", "Python", "MongoDB")));
        freelancer1.setIsActive(true);
        freelancer1.setIsVerified(true);
        freelancer1.setReputationPoints(150);
        userRepository.save(freelancer1);

        User freelancer2 = new User();
        freelancer2.setUsername("maria_designer");
        freelancer2.setEmail("maria@design.com");
        freelancer2.setPassword(passwordEncoder.encode("password123"));
        freelancer2.setBio("UI/UX designer passionate about creating beautiful user experiences");
        freelancer2.setSkills(new HashSet<>(Arrays.asList("Figma", "Adobe XD", "Sketch", "Prototyping")));
        freelancer2.setIsActive(true);
        freelancer2.setIsVerified(true);
        freelancer2.setReputationPoints(120);
        userRepository.save(freelancer2);
    }

    private void seedProjects() {
        User client1 = userRepository.findByUsername("john_client").orElse(null);
        User client2 = userRepository.findByUsername("sarah_startup").orElse(null);

        if (client1 != null) {
            // Project 1
            Project project1 = new Project();
            project1.setTitle("E-commerce Website Development");
            project1.setDescription("Looking for a skilled developer to build a modern e-commerce website with React and Node.js. The site should include user authentication, product catalog, shopping cart, and payment integration.");
            project1.setClient(client1);
            project1.setBudget(new BigDecimal("2500"));
            project1.setDeadline(LocalDateTime.now().plusDays(30));
            project1.setRequiredSkills(new HashSet<>(Arrays.asList("React", "Node.js", "MongoDB", "Stripe")));
            project1.setType(Project.ProjectType.PAID);
            project1.setStatus(Project.ProjectStatus.OPEN);
            projectRepository.save(project1);

            // Project 2
            Project project2 = new Project();
            project2.setTitle("Mobile App for Fitness Tracking");
            project2.setDescription("Need a React Native developer to create a fitness tracking app with GPS tracking, workout logging, and social features. Should integrate with health APIs and have a clean, intuitive UI.");
            project2.setClient(client1);
            project2.setBudget(new BigDecimal("1800"));
            project2.setDeadline(LocalDateTime.now().plusDays(45));
            project2.setRequiredSkills(new HashSet<>(Arrays.asList("React Native", "Firebase", "GPS", "Health APIs")));
            project2.setType(Project.ProjectType.PAID);
            project2.setStatus(Project.ProjectStatus.OPEN);
            projectRepository.save(project2);
        }

        if (client2 != null) {
            // Project 3
            Project project3 = new Project();
            project3.setTitle("UI/UX Design for SaaS Platform");
            project3.setDescription("Seeking a talented UI/UX designer to create modern, user-friendly designs for our SaaS platform. Need wireframes, mockups, and design system. Focus on conversion optimization and user experience.");
            project3.setClient(client2);
            project3.setBudget(new BigDecimal("1200"));
            project3.setDeadline(LocalDateTime.now().plusDays(25));
            project3.setRequiredSkills(new HashSet<>(Arrays.asList("Figma", "Adobe XD", "Prototyping", "User Research")));
            project3.setType(Project.ProjectType.PAID);
            project3.setStatus(Project.ProjectStatus.OPEN);
            projectRepository.save(project3);

            // Project 4
            Project project4 = new Project();
            project4.setTitle("Backend API Development");
            project4.setDescription("Looking for a backend developer to build a RESTful API for our startup. Should include user management, data processing, and third-party integrations. Prefer Python/Django or Node.js/Express.");
            project4.setClient(client2);
            project4.setBudget(new BigDecimal("3000"));
            project4.setDeadline(LocalDateTime.now().plusDays(40));
            project4.setRequiredSkills(new HashSet<>(Arrays.asList("Python", "Django", "PostgreSQL", "AWS")));
            project4.setType(Project.ProjectType.PAID);
            project4.setStatus(Project.ProjectStatus.OPEN);
            projectRepository.save(project4);
        }

        // Additional projects
        Project project5 = new Project();
        project5.setTitle("WordPress Website Redesign");
        project5.setDescription("Need to redesign an existing WordPress website with modern design and improved performance. Should be mobile-responsive and SEO-optimized.");
        project5.setClient(client1);
        project5.setBudget(new BigDecimal("800"));
        project5.setDeadline(LocalDateTime.now().plusDays(20));
        project5.setRequiredSkills(new HashSet<>(Arrays.asList("WordPress", "PHP", "CSS", "SEO")));
        project5.setType(Project.ProjectType.PAID);
        project5.setStatus(Project.ProjectStatus.OPEN);
        projectRepository.save(project5);

        Project project6 = new Project();
        project6.setTitle("Data Analysis Dashboard");
        project6.setDescription("Create an interactive dashboard for data visualization using Python and modern web technologies. Should handle real-time data updates and provide insights.");
        project6.setClient(client2);
        project6.setBudget(new BigDecimal("2200"));
        project6.setDeadline(LocalDateTime.now().plusDays(35));
        project6.setRequiredSkills(new HashSet<>(Arrays.asList("Python", "D3.js", "Flask", "Pandas")));
        project6.setType(Project.ProjectType.PAID);
        project6.setStatus(Project.ProjectStatus.OPEN);
        projectRepository.save(project6);
    }
} 