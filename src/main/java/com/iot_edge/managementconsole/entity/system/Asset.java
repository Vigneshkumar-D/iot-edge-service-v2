package com.iot_edge.managementconsole.entity.system;

import com.iot_edge.common.Auditable;
import com.iot_edge.managementconsole.constants.AssetCategory;
import com.iot_edge.managementconsole.model.system.Parameters;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "assets")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String assetName;

    @Column(length = 10000)
    private String scriptCode;

    @Column(nullable = false)
    private AssetCategory assetCategory;

//    @ManyToOne
//    @JoinColumn(name = "location_id", nullable = false)
//    private Location location;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean isActive;

    private String clientId;

    private String subTopicName;

    private String pubTopicName;

    @ManyToOne
    @JoinColumn(name = "firm_uuid", referencedColumnName = "uuid", nullable = false)  // Ensure firm_id exists
//    @JsonIgnoreProperties(ignoreUnknown = true)
    private Firm firm;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Parameters> parameters =  new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }

}
