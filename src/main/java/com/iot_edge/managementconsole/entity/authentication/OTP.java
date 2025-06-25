package com.iot_edge.managementconsole.entity.authentication;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iot_edge.common.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@Table(name = "otp")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NoArgsConstructor
@AllArgsConstructor
public class OTP extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="contact_no")
    private String contactNo;

    @Column(name="email")
    private String email;

    @Column(name="otp_contact_no")
    private String otpContactNo;

    @Column(name="otp_email")
    private String otpEmail;

    @Column(name="otp_contact_no_created_at")
    private Date otpContactNoCreatedAt;

    @Column(name="otp_email_created_at")
    private Date otpEmailCreatedAt;

    @Column(name="otp_expires_at")
    private Date otpExpiresAt;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private Date deletedAt;

}

