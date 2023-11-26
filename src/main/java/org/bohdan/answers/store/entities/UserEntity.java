package org.bohdan.answers.store.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bohdan.answers.api.domain.Views;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Email(message = "Please, provide a valid email.")
    @Column(name = "email")
    @JsonView(Views.FullProfile.class)
    String email;

    @NotBlank(message = "Please, provide full name.")
    @Column(name = "full_name")
    @JsonView(Views.FullProfile.class)
    String fullName;

    @NotBlank(message = "Please, provide username.")
    @Column(name = "username")
    @JsonView(Views.FullProfile.class)
    String username;

    @NotBlank(message = "Please, provide phone number.")
    @Column(name = "phone_number")
    @JsonView(Views.FullProfile.class)
    String phoneNumber;

    @NotBlank(message = "Please, enter password.")
    @Column(name = "password")
    String password;

    @Transient
    String repeatPassword;

    @Transient
    @JsonView(Views.FullProfile.class)
    String userOnline;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.FullProfile.class)
    private Role role;

    @Builder.Default
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @JsonView(Views.FullProfile.class)
    Instant createdAt = Instant.now();

    @OneToOne(mappedBy = "user")
    ChatEntity chat;
}
