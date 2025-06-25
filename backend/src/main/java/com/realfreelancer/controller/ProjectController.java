package com.realfreelancer.controller;

import com.realfreelancer.model.Project;
import com.realfreelancer.model.Application;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.repository.ApplicationRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.service.ProjectService;
import com.realfreelancer.dto.ProjectRequest;
import com.realfreelancer.dto.ApplicationRequest;
import com.realfreelancer.dto.ProjectDTO;
import com.realfreelancer.dto.ClientDTO;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:3000")
class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all projects with pagination and filtering
    @GetMapping
    public ResponseEntity<?> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget,
            @RequestParam(required = false) String search
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Project> projects = projectService.getFilteredProjects(
                status, type, skills, minBudget, maxBudget, search, pageable
            );
            // Map to DTOs
            List<ProjectDTO> dtos = projects.getContent().stream().map(project -> {
                ProjectDTO dto = new ProjectDTO();
                dto.setId(project.getId());
                dto.setTitle(project.getTitle());
                dto.setDescription(project.getDescription());
                dto.setBudget(project.getBudget());
                dto.setDeadline(project.getDeadline());
                dto.setRequiredSkills(project.getRequiredSkills());
                dto.setStatus(project.getStatus().name());
                dto.setType(project.getType().name());
                if (project.getClient() != null) {
                    dto.setClient(new ClientDTO(
                        project.getClient().getId(),
                        project.getClient().getUsername(),
                        project.getClient().getEmail()
                    ));
                }
                return dto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching projects: Server error");
        }
    }

    // Get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        try {
            Optional<Project> project = projectRepository.findById(id);
            if (project.isPresent()) {
                // Increment view count
                Project p = project.get();
                p.incrementViewCount();
                projectRepository.save(p);
                ProjectDTO dto = new ProjectDTO();
                dto.setId(p.getId());
                dto.setTitle(p.getTitle());
                dto.setDescription(p.getDescription());
                dto.setBudget(p.getBudget());
                dto.setDeadline(p.getDeadline());
                dto.setRequiredSkills(p.getRequiredSkills());
                dto.setStatus(p.getStatus().name());
                dto.setType(p.getType().name());
                if (p.getClient() != null) {
                    dto.setClient(new ClientDTO(
                        p.getClient().getId(),
                        p.getClient().getUsername(),
                        p.getClient().getEmail()
                    ));
                }
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching project: Project not found");
        }
    }

    // Create new project
    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        try {
            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

            // Validate budget
            if (projectRequest.getBudget() == null || projectRequest.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body("Budget must be zero or positive");
            }

            // Create project
            Project project = new Project();
            project.setTitle(projectRequest.getTitle());
            project.setDescription(projectRequest.getDescription());
            project.setRequiredSkills(projectRequest.getRequiredSkills());
            project.setBudget(projectRequest.getBudget());
            project.setDeadline(projectRequest.getDeadline());
            project.setType(projectRequest.getType());
            project.setClient(client);

            Project savedProject = projectRepository.save(project);
            return ResponseEntity.ok(savedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid project type: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating project: " + e.getMessage());
        }
    }

    // Update project
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest projectRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<Project> projectOpt = projectRepository.findById(id);
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            if (!project.getClient().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only update your own projects");
            }

            project.setTitle(projectRequest.getTitle());
            project.setDescription(projectRequest.getDescription());
            project.setRequiredSkills(projectRequest.getRequiredSkills());
            project.setBudget(projectRequest.getBudget());
            project.setDeadline(projectRequest.getDeadline());
            project.setType(projectRequest.getType());

            Project updatedProject = projectRepository.save(project);
            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating project: Invalid project data");
        }
    }

    // Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<Project> projectOpt = projectRepository.findById(id);
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            if (!project.getClient().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only delete your own projects");
            }

            projectRepository.delete(project);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting project: Project not found");
        }
    }

    // Apply for project
    @PostMapping("/{id}/apply")
    public ResponseEntity<?> applyForProject(@PathVariable Long id, @Valid @RequestBody ApplicationRequest applicationRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User freelancer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Project> projectOpt = projectRepository.findById(id);
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            if (project.getClient().getId().equals(freelancer.getId())) {
                return ResponseEntity.badRequest().body("You cannot apply for your own project");
            }

            if (project.getStatus() != Project.ProjectStatus.OPEN) {
                return ResponseEntity.badRequest().body("Project is not open for applications");
            }

            // Check if already applied
            if (applicationRepository.existsByProjectAndFreelancer(project, freelancer)) {
                return ResponseEntity.badRequest().body("You have already applied for this project");
            }

            Application application = new Application();
            application.setPitch(applicationRequest.getPitch());
            application.setProposedBudget(applicationRequest.getProposedBudget());
            application.setEstimatedDurationDays(applicationRequest.getEstimatedDurationDays());
            application.setProject(project);
            application.setFreelancer(freelancer);
            application.setAttachmentUrl(applicationRequest.getAttachmentUrl());

            Application savedApplication = applicationRepository.save(application);
            
            // Increment application count
            project.incrementApplicationCount();
            projectRepository.save(project);

            return ResponseEntity.ok(savedApplication);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error applying for project: Invalid application data");
        }
    }

    // Get applications for a project
    @GetMapping("/{id}/applications")
    public ResponseEntity<?> getProjectApplications(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<Project> projectOpt = projectRepository.findById(id);
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            if (!project.getClient().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only view applications for your own projects");
            }

            List<Application> applications = applicationRepository.findByProject(project, PageRequest.of(0, 100)).getContent();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching applications: Server error");
        }
    }

    // Accept/Reject application
    @PutMapping("/{projectId}/applications/{applicationId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long projectId,
            @PathVariable Long applicationId,
            @RequestParam String status
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<Project> projectOpt = projectRepository.findById(projectId);
            if (!projectOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = projectOpt.get();
            if (!project.getClient().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only update applications for your own projects");
            }

            Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
            if (!applicationOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Application application = applicationOpt.get();
            if (!application.getProject().getId().equals(projectId)) {
                return ResponseEntity.badRequest().body("Application does not belong to this project");
            }

            switch (status.toUpperCase()) {
                case "ACCEPTED":
                    application.setStatus(Application.ApplicationStatus.ACCEPTED);
                    project.setStatus(Project.ProjectStatus.IN_PROGRESS);
                    project.setFreelancer(application.getFreelancer());
                    break;
                case "REJECTED":
                    application.setStatus(Application.ApplicationStatus.REJECTED);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid status. Use 'ACCEPTED' or 'REJECTED'");
            }

            applicationRepository.save(application);
            projectRepository.save(project);

            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating application: Invalid application data");
        }
    }

    // Get user's projects (as client)
    @GetMapping("/my-projects")
    public ResponseEntity<?> getMyProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Project> projects = projectRepository.findByClient(user, pageable);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching projects: Server error");
        }
    }

    // Get user's applications (as freelancer)
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Application> applications = applicationRepository.findByFreelancer(user, pageable);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching applications: Server error");
        }
    }

    // Get featured projects
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedProjects() {
        try {
            List<Project> featuredProjects = projectRepository.findFeaturedOpenProjects();
            return ResponseEntity.ok(featuredProjects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching featured projects: Server error");
        }
    }
} 