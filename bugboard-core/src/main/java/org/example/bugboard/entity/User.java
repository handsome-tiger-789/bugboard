package org.example.bugboard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role;

    @Builder
    public User(String provider, String providerId, String email, String name, String nickname, String role) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
    }

    public void update(String nickname) {
        this.nickname = nickname;
    }
}
