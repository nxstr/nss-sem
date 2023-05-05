package cz.cvut.fel.nss.chatgc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends AbstractEntity{

    private String name;

    @ManyToMany
    private Set<Category> categories;

//    private int depth;

    @ManyToOne
    private Role parentRole;

    @OneToMany(mappedBy = "parentRole")
    private Set<Role> childrenRoles;
}
