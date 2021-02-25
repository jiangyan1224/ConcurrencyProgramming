package design.ch6;

public class MethodReferenceTest {
    @FunctionalInterface
    interface UserFactory<U extends User>{
        U create(int id, String name);
    }
    static UserFactory<User> uf = User::new;//反编译用到Lambda.metaFactory，好像还用到了invoke，没再往下看了
//static UserFactory<User> uf = (UserFactory<User>) new User(1,"12");
    public static void main(String[] args){
        System.out.println(uf.getClass());
        System.out.println(uf.toString());
    }
}

