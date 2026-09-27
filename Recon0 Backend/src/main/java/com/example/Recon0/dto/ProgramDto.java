package com.example.Recon0.dto;

import com.example.Recon0.models.Program;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ProgramDto implements Serializable {
    private UUID id;
    private UUID organization_id;
    private String org_name;
    private String title;
    private Integer min_bounty;
    private Integer max_bounty;
    private String[] tags;
    private String org_logo_url;

    public static ProgramDto fromProgram(Program program) {
        if (program == null) {
            return null;
        }
        ProgramDto dto = new ProgramDto();
        dto.setId(program.getId());
        String orgName = null;
        String orgLogoUrl = null;
        if (program.getOrganization_id() != null) {
            dto.setOrganization_id(program.getOrganization_id().getId());
            orgName = program.getOrganization_id().getFull_name();
            if (orgName == null || orgName.isBlank()) {
                orgName = program.getOrganization_id().getDisplayName();
            }
            if (orgName == null || orgName.isBlank()) {
                orgName = program.getOrganization_id().getUsername();
            }
            orgLogoUrl = program.getOrganization_id().getAvatar_url();
        }
        if (orgName == null || orgName.isBlank()) {
            orgName = program.getOrg_name();
        }
        if (orgName == null || orgName.isBlank()) {
            orgName = "Organization";
        }
        dto.setOrg_name(orgName);
        dto.setOrg_logo_url(orgLogoUrl);
        dto.setTitle(program.getTitle());
        dto.setMin_bounty(program.getMin_bounty());
        dto.setMax_bounty(program.getMax_bounty());
        dto.setTags(program.getTags());
        return dto;
    }
}
