package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.DataGenerator;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.repository.CategoryRepository;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private CategoryRepository categoryDao;

    @Mock
    private RoleRepository roleDao;

    @InjectMocks
    private RoleService roleService;

    @Test
    public void persistRoleWithParentTest(){
        Role role = setUpRole(3);
        Role parent = roleService.persist(role);

        Role child = setUpRole(1);
        child.setParentRole(parent);
        Role actual = roleService.persist(child);

        Set<Category> cats1 = new HashSet<>(role.getCategories());
        cats1.addAll(child.getCategories());
        Set<Role> children = new HashSet<>();
        children.add(actual);

        assertEquals(cats1, actual.getCategories());
        assertEquals(parent, actual.getParentRole());
        assertEquals(children, parent.getChildrenRoles());
        Mockito.verify(roleDao, Mockito.times(1)).save(actual);
    }

    @Test
    public void changeRoleParentTest(){
        Role role = setUpRole(3);
        Role parent1 = roleService.persist(role);

        Role role1 = setUpRole(2);
        Role parent2 = roleService.persist(role1);


        Role child = setUpRole(1);
        child.setParentRole(parent1);
        Role actualChild = roleService.persist(child);

        roleService.changeRoleParent(actualChild, parent2);

        assertEquals(parent2.getName(), actualChild.getParentRole().getName());

        Set<Role> children = new HashSet<>();
        children.add(actualChild);

        assertEquals(new HashSet<Role>(), parent1.getChildrenRoles());
        assertEquals(children, parent2.getChildrenRoles());


        Mockito.verify(roleDao, Mockito.times(3)).save(actualChild);
    }


    @Test
    public void RemoveRoleCategoryTest(){
        Role role = setUpRole(3);
        Role parent1 = roleService.persist(role);

        Role child = setUpRole(1);
        child.setParentRole(parent1);
        Role actualChild = roleService.persist(child);

        Category removed = null;
        for(Category c: parent1.getCategories()){
            removed = c;
            break;
        }



        roleService.removeRoleCategory(parent1, removed);

        assertEquals(actualChild.getCategories(), parent1.getChildrenRoles().stream().findFirst().orElse(null).getCategories());
        Category finalRemoved = removed;
        assertEquals(0, parent1.getCategories().stream().filter(d-> {
            assert finalRemoved != null;
            return Objects.equals(d.getName(), finalRemoved.getName());
        }).count());
        assertEquals(2, parent1.getCategories().size());
        assertEquals(3, actualChild.getCategories().size());

        Mockito.verify(roleDao, Mockito.times(3)).save(parent1);
    }

    @Test
    public void findByName(){
        Role role = DataGenerator.generateEmptyRole();
        Role act = roleService.persist(role);

        roleService.findByName(act.getName());

        Mockito.verify(roleDao, Mockito.times(1)).findByName(act.getName());
    }

    @Test
    public void updateRoleName(){
        Role role = setUpRole(3);
        Role parent1 = roleService.persist(role);

        Role child = setUpRole(1);
        child.setParentRole(parent1);
        Role actualChild = roleService.persist(child);

        roleService.updateRoleName(parent1, "test");

        assertEquals("test", parent1.getName());
        assertEquals("test", actualChild.getParentRole().getName());
    }

    @Test
    public void addAndRemoveChild(){
        Role role = setUpRole(3);
        Role parent1 = roleService.persist(role);

        Role child = setUpRole(1);
        child.setParentRole(parent1);
        Role actualChild1 = roleService.persist(child);

        Role child2 = setUpRole(2);
        Role actualRole = roleService.persist(child2);

        roleService.addChild(parent1, actualRole);

        assertEquals(2, parent1.getChildrenRoles().size());
//        assertTrue(parent1.getChildrenRoles().contains(actualRole));
        assertEquals(1, parent1.getChildrenRoles().stream().filter(d-> Objects.equals(d.getName(), actualRole.getName())).count());
        assertEquals(parent1, actualRole.getParentRole());

        roleService.removeChild(actualChild1);

        assertNull(actualChild1.getParentRole());
        assertEquals(1, parent1.getChildrenRoles().size());
        assertEquals(0, parent1.getChildrenRoles().stream().filter(d-> Objects.equals(d.getName(), actualChild1.getName())).count());
    }


    private Role setUpRole(int countCats){
        Set<Category> cats = new HashSet<>();
        for(int i=0; i<countCats; i++){
            Category cat1 = DataGenerator.generateCategory();
            categoryDao.save(cat1);
            cats.add(cat1);
        }
        Role role = DataGenerator.generateEmptyRole();
        role.setCategories(cats);
        return role;
    }
}
