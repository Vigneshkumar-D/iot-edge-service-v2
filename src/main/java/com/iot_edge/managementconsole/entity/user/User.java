package com.iot_edge.managementconsole.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Table(name = "user_details")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class User extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password may not be blank")
    @JsonIgnore
    private String password;

    @Column(nullable = false, unique = true, updatable = false)
    @Email(message = "Email should be valid")
    private String email;

    @Builder.Default
    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @Builder.Default
    @Column(name = "is_contact_no_verified")
    private Boolean isContactNoVerified = false;

//    @Lob
//    private byte[] profileImage;

    @Builder.Default
    @Column(name = "avatar_url")
    private String avatarUrl = "https://github.com/shadcn.png";

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    @ManyToOne
    @JoinColumn(name = "role_uuid", referencedColumnName = "uuid")
    private Role role;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "last_login")
    private Instant lastLogin;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
