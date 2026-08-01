package com.example.Recon0.dto.reports;

import com.example.Recon0.dto.reports.AttachmentDto;
import lombok.Data;

import java.util.List;

@Data
public class SendMessageRequest {
    private String content;
    private List<AttachmentDto> attachments;
}
