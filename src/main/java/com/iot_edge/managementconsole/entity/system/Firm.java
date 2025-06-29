package com.iot_edge.managementconsole.entity.system;

import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "firm")
public class Firm extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")  // Defines the column name for the parent reference
    private Firm parent;

    @Column(name = "contact_no")
    private String contactNo;

    private String email;

    private String website;

//    @OneToOne
//    @JoinColumn(name = "logo_uuid", referencedColumnName = "uuid")
//    private Upload logo;

    @Column(name = "logo_url")
    private String logoUrl;


    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false) // Adds a foreign key column in 'firm' table
    private Location location;

    @PrePersist
    protected void onCreate() {
        this.uuid = UUID.randomUUID();
    }

}


