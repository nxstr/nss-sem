package cz.cvut.fel.nss.chatgc.repository;

import cz.cvut.fel.nss.chatgc.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    public Role findByName(String name);
};
