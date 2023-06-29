package cz.cvut.fel.nss.chatgc.mapper;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;
import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.service.CategoryService;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class VisitorImpl implements Visitor{

    private final RoleService roleService;
    private final ChatService chatService;
    private final CategoryService categoryService;

    public VisitorImpl(RoleService roleService, ChatService chatService, CategoryService categoryService) {
        this.roleService = roleService;
        this.chatService = chatService;
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDto visitCategoryEntity(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    @Override
    public Category visitCategoryDto(CategoryDto categoryDto) {
        Category category = new Category(categoryDto.getName());
        if(categoryDto.getId()!=null){
            category.setId(categoryDto.getId());
        }
        return category;
    }





    @Override
    public EmployeeDTO visitEmployeeEntity(Employee employee) {
        return new EmployeeDTO(employee.getUsername(), "", employee.getEmail(), employee.getRole().getId(), employee.getRole().getName(), employee.getId());
    }

    @Override
    public Employee visitEmployeeDto(EmployeeDTO employeeDTO) {
        Role role = roleService.findById(employeeDTO.getRoleId()).orElse(null);
        if(role==null){
            throw new RoleException("employee can not be created without role");
        }
        Employee employee = new Employee(employeeDTO.getUsername(), employeeDTO.getEmail(), employeeDTO.getPassword(), role);
        employee.setResponses(new ArrayList<>());
        if(employeeDTO.getId()!=null){
            employee.setId(employeeDTO.getId());
        }
        return employee;
    }

    @Override
    public PlayerDto visitPlayerEntity(Player player) {
        return new PlayerDto(player.getUsername(), player.getEmail(), "", player.getId());
    }

    @Override
    public Player visitPlayerDto(PlayerDto playerDto) {
        Player player = new Player(playerDto.getUsername(), playerDto.getEmail(), playerDto.getPassword(), PlayerRoles.REGISTERED);
        Chat chat = chatService.findByPlayer(playerDto.getUsername());
        player.setChat(chat);
        if(playerDto.getId()!=null){
            player.setId(playerDto.getId());
        }
        return player;
    }

    @Override
    public RoleDto visitRoleEntity(Role role) {
        List<CategoryDto> cats = new ArrayList<>();
        for(Category c: role.getCategories()){
            cats.add(c.accept(this));
        }
        Integer parentId = null;
        if(role.getParentRole()!=null){
            parentId = role.getParentRole().getId();
        }
        return new RoleDto(role.getName(), cats, parentId, role.getId());
    }

    @Override
    public Role visitRoleDto(RoleDto roleDto) {
        Set<Category> cats = new HashSet<>();
        for(CategoryDto c: roleDto.getCategoryDtoList()){
            if (categoryService.findById(c.getId()) != null) {
                cats.add(categoryService.findById(c.getId()));
            }
        }
        Role parent = null;
        if(roleDto.getParentId()!=null) {
             parent = roleService.findById(roleDto.getParentId()).orElse(null);
        }
        return new Role(roleDto.getName(), cats, parent, new HashSet<>());
    }
}
