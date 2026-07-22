package com.lionproject24.fruitshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Builder 사용을 하기 위함.
@Builder // AuthService에서 객채 만들기 위해 사용.
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId; // Entity.User.id 값을 참조
    private String token;
}
