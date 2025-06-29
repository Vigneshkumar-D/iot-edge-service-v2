package com.iot_edge.managementconsole.entity.user;

import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.entity.system.Firm;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class Role extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(unique = true)
    @NotBlank(message = "Role name may be blank")
    private String roleName;

    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "firm_uuid", referencedColumnName = "uuid")
    private Firm firm;

    @SneakyThrows
    @PreRemove
    private void preRemove(){
        List<String> roles = Arrays.asList("Super Admin","Admin","Manager","Guest");
        if(roles.contains(this.roleName)){
            throw new RuntimeException("Predefined Role : \""+this.roleName+"\" can't be deleted");
        }
    }

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
