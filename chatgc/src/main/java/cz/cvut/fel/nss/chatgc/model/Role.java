package cz.cvut.fel.nss.chatgc.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends AbstractEntity{

    @Getter
    @Setter
    private String name;

    @ManyToMany
    @Getter
    @Setter
    private Set<Category> categories;

//    private int depth;

    @ManyToOne
    @Getter
    @Setter
    private Role parentRole;

    @OneToMany(mappedBy = "parentRole")
    @Getter
    @Setter
    private Set<Role> childrenRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name) && Objects.equals(categories, role.categories) && Objects.equals(parentRole, role.parentRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, categories, parentRole);
    }
}
