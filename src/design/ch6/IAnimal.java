package design.ch6;

public interface IAnimal {
//    void run();
    default void run(){
        System.out.println();
    }
    default void breath() {

    }
}
