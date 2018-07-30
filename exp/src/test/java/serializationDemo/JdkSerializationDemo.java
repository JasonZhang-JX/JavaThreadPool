package serializationDemo;

import java.io.*;
import java.util.Arrays;

public class JdkSerializationDemo {
    public static void main(String[] args) {
        User user = new User();
        user.setId(1L);
        user.setName("Ricky");
        user.setPassword("root");
        user.setAge(28);
        user.setHobbies(Arrays.asList("Music", "Basketball"));
        System.out.println(user);

        File objectFile = new File("user.bin");

        //将对象存到文件里面(序列化)
        //Write Obj to File
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(objectFile));
            oos.writeObject(user);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将文件反序列化为对象
        //Read Obj from File
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(objectFile));
            User newUser = (User) ois.readObject();
            System.out.println(newUser);
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
