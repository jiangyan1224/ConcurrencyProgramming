package design.ch6;

public interface IHorse {
    default void run(){
        System.out.println("IHorse run");
    }
}
