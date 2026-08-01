package com.example.Recon0.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private User organization_id;

    private String org_name;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Lob
    private String policy;

    @Lob
    private String scope;

    @Lob
    private String out_of_scope;

    private Integer min_bounty;

    private Integer max_bounty;

    @Column(columnDefinition = "text[]")
    private String[] tags;
}
