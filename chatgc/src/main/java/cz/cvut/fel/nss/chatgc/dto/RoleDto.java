package cz.cvut.fel.nss.chatgc.dto;

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
}
