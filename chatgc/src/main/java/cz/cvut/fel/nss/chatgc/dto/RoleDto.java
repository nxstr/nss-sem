package cz.cvut.fel.nss.chatgc.dto;

import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDto {
    String name;
    List<CategoryDto> categoryDtoList;
    Integer parentId;
    Integer id;

    public Role accept(Visitor v) {
        return v.visitRoleDto(this);
    }
}
