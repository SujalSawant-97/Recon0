package com.example.Recon0.dto;

import com.example.Recon0.models.Program;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProgramDetailDto extends ProgramDto {
    private String description;
    private String policy;
    private String scope;
    private String out_of_scope;

    public static ProgramDetailDto fromProgram(Program program) {
        ProgramDetailDto dto = new ProgramDetailDto();
        // Set fields from parent DTO
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

        // Set detail fields
        dto.setDescription(program.getDescription());
        dto.setPolicy(program.getPolicy());
        dto.setScope(program.getScope());
        dto.setOut_of_scope(program.getOut_of_scope());
        return dto;
    }
}