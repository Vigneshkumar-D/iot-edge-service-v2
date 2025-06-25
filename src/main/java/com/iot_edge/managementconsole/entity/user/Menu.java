package com.iot_edge.managementconsole.entity.user;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu")
public class Menu extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "uuid")
    private UUID uuid;
    private String menuName;
    private Integer parentId;
    private UUID pageId;
    private String icon;
    private String path;
    private Integer orderNumber;
    private Boolean status;
    private String moduleName;
    private List<Integer> userAccessFeatureMappings;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }
}
