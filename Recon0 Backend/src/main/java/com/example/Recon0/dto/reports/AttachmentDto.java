package com.example.Recon0.dto.reports;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentDto {
    private String id;

    @JsonProperty("url")
    @JsonAlias({"url", "file_url", "fileUrl"})
    private String url;

    @JsonProperty("name")
    @JsonAlias({"name", "file_name", "fileName"})
    private String name;

    @JsonProperty("type")
    @JsonAlias({"type", "file_type", "fileType"})
    private String type;

    @JsonProperty("uploadedAt")
    @JsonAlias({"uploadedAt", "uploaded_at"})
    private String uploadedAt;

    @JsonProperty("file_url")
    public String getFile_url() {
        return url;
    }

    @JsonProperty("file_name")
    public String getFile_name() {
        return name;
    }

    @JsonProperty("file_type")
    public String getFile_type() {
        return type;
    }

    @JsonProperty("uploaded_at")
    public String getUploaded_at() {
        return uploadedAt;
    }
}
