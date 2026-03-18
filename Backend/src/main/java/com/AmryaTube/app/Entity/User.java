package com.AmryaTube.app.Entity;

import com.AmryaTube.app.Enums.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
//    I deleted it Because the from oAuth2 maybe less than 3
//    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    @Column(nullable = false)
    private String name;

    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Email cannot be Empty")
    @Email(message = "Invalid Email, Please Enter Valid Email")
    @Column(unique = true, nullable = false)
    private String email;
//    I deleted it because when user login with oAuth2 password is null
//    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    private String providerId;


    @Column(nullable = false)
    private Boolean isEnabled = true;

    @Column(nullable = false)
    private Boolean isAccountNonExpired = true;


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @OneToMany(mappedBy = "uploader")
    Set<Video> videos =new HashSet<>();

    @OneToMany(mappedBy = "subscriber")
    private Set<Subscription> subscriptions = new HashSet<>();
}