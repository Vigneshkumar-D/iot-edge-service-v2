package com.iot_edge.managementconsole.entity.password;

import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_management")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordManagement extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration")
    private Instant expiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid", nullable = false)
    private User user;

    public boolean isExpired() {
        return Instant.now().isAfter(expiration);
    }

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
