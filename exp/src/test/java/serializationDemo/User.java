package serializationDemo;

import java.io.Serializable;
import java.util.List;


/**
 * 待序列化的Java类只需要实现java.io.Serializable接口即可。
 * Serializable仅是一个标记接口，并不包含任何需要实现的具体方法。
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String password;
    private int age;
    private List<String> hobbies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }
}
