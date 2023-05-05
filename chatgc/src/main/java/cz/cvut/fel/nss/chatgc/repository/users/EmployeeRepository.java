package cz.cvut.fel.nss.chatgc.repository.users;

import cz.cvut.fel.nss.chatgc.model.users.Employee;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends UserRepository<Employee, Integer>{

}
