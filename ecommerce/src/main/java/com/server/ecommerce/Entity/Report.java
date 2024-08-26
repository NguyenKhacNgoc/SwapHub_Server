package com.server.ecommerce.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accuser", referencedColumnName = "id")
    private User accuser;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accused", referencedColumnName = "id")
    private User accused;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Posts post;
    @Column
    private String reason;
    @Column
    private LocalDateTime sendAt;
    @Column
    private String result;
    @Column
    private String status;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verifier", referencedColumnName = "id")
    private User verifier;
    @Column
    private LocalDateTime verifyAt;
    @Column
    private String action;

}
