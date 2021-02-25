package design.ch6;

public class Mule implements IAnimal,IHorse {
    public static void main(String[] args){
        Mule mule = new Mule();
        mule.run();
    }

    @Override
    public void run() {
        IHorse.super.run();
    }
}
