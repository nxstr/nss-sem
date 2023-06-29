package cz.cvut.fel.nss.chatgc;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;

import java.util.HashSet;
import java.util.Random;

public class DataGenerator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }


    public static Category generateCategory(){
        return new Category("testcat" + randomInt());
    }


    public static Role generateEmptyRole(){
        return new Role("testrole" + randomInt(), new HashSet<>(), null, new HashSet<>());
    }

    public static Employee generateEmployee(){
        return new Employee("test"+randomInt(), "email"+randomInt()+"@aaa.aaa", "pass"+randomInt(), null);
    }
}
