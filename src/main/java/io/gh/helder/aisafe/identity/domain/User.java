package io.gh.helder.aisafe.identity.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "users")
public class User {

    private static final int SKILLS_ASSESSMENT_YEARS = 5;

    @Id
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "email", nullable = false, length = 254))
    private Email email;

    @Column(name = "password_hash", nullable = false, length = 72)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = EnumSet.noneOf(Role.class);

    @Column(name = "clearance_expires_on", nullable = false)
    private LocalDate clearanceExpiresOn;

    @Column(name = "skills_assessed_on", nullable = false)
    private LocalDate skillsAssessedOn;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private long version;

    protected User() { }   // JPA only

    public User(Email email, String passwordHash, String name, String phoneNumber,
                Set<Role> roles, LocalDate clearanceExpiresOn, LocalDate skillsAssessedOn) {
        this.id = UUID.randomUUID();
        this.email = Objects.requireNonNull(email, "email is required");
        this.passwordHash = requireText(passwordHash, "password hash");
        this.name = requireText(name, "name");
        this.phoneNumber = requireText(phoneNumber, "phone number");
        this.clearanceExpiresOn = Objects.requireNonNull(clearanceExpiresOn,
                "clearance expiry is required");
        this.skillsAssessedOn = Objects.requireNonNull(skillsAssessedOn,
                "skills assessment date is required");

        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("at least one role is required");
        }
        this.roles = EnumSet.copyOf(roles);
        this.enabled = true;
        this.createdAt = Instant.now();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    public void enable()  { this.enabled = true; }
    public void disable() { this.enabled = false; }

    public void renewClearance(LocalDate expiresOn) {
        if (expiresOn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("clearance expiry must be in the future");
        }
        this.clearanceExpiresOn = expiresOn;
    }

    public void recordSkillsAssessment(LocalDate assessedOn) {
        if (assessedOn.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("assessment date cannot be in the future");
        }
        this.skillsAssessedOn = assessedOn;
    }

    public void changePassword(String newHash) {
        this.passwordHash = requireText(newHash, "password hash");
    }

    public boolean clearanceValid() {
        return !clearanceExpiresOn.isBefore(LocalDate.now());
    }

    public boolean skillsAssessmentCurrent() {
        return !skillsAssessedOn.plusYears(SKILLS_ASSESSMENT_YEARS).isBefore(LocalDate.now());
    }

    public boolean canAuthenticate() {
        return enabled && clearanceValid() && skillsAssessmentCurrent();
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public UUID id()                      { return id; }
    public Email email()                  { return email; }
    public String passwordHash()          { return passwordHash; }
    public String name()                  { return name; }
    public String phoneNumber()           { return phoneNumber; }
    public boolean enabled()              { return enabled; }
    public Set<Role> roles()              { return Set.copyOf(roles); }
    public LocalDate clearanceExpiresOn() { return clearanceExpiresOn; }
    public LocalDate skillsAssessedOn()   { return skillsAssessedOn; }
    public Instant createdAt()            { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User[" + email + "]";
    }
}
