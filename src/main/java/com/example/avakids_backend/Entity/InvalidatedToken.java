package com.example.avakids_backend.Entity;

import java.util.Date;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invalidated_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidatedToken {

    @Id
    @Column(name = "token_id", length = 36)
    private String id;

    @Column(name = "expiry_time", nullable = false)
    private Date expiryTime;
}
