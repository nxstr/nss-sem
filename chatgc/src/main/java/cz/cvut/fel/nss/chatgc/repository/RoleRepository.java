package cz.cvut.fel.nss.chatgc.repository;

import cz.cvut.fel.nss.chatgc.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Searching for role by role name.
     * @param name means role name
     * @return Role
     */

    public Role findByName(String name);
};
