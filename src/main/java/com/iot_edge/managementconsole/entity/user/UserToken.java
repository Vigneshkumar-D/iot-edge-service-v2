package com.iot_edge.managementconsole.entity.user;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="user_token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserToken extends Auditable<String> implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(columnDefinition="TEXT")
    private String token;

    private Integer userId;

    private Instant expireOn;

    private Integer amount;

    private String unit;

    @ReadOnlyProperty
    @OneToOne(orphanRemoval = false)
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid",insertable = false,updatable = false)
    private User user;

    @Builder.Default
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
