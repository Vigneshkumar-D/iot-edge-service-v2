package com.iot_edge.managementconsole.entity.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@Table(name = "otp_attempts")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NoArgsConstructor
@AllArgsConstructor
public class OtpAttempt extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "contact_no")
    private String contactNo;

    private String email;

    @Column(name = "no_of_attempts")
    private Integer noOfAttempts;

    @Column(name = "to_be_deleted_at")
    private LocalDateTime toBeDeletedAt;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}

