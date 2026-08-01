package com.example.Recon0.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private String id;
    private String url;
    private String name;
    private String type;
    private String uploadedAt;
}
