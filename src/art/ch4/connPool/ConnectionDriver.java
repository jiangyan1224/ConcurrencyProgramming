package art.ch4.connPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**代理Connection对象的代理类*/
public class ConnectionDriver {
    static class ConnectionHandler implements InvocationHandler{
        private Object realObj;
        public ConnectionHandler(Object realObj){
            this.realObj = realObj;
        }
        public ConnectionHandler(){}
        /**生成代理类class文件，包含被代理对象实现的所有接口中定义的所有方法；
         * 所有调用被代理对象中实现的接口方法  --> 转换成对invoke方法的调用，最终是Method.invoke()方法，调用实际被代理对象实例的对应方法
         * @param proxy 代理对象实例
         * @param method method对象
         * @param args 传入method对象的参数
         * @return 方法返回对象
         *  */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("commit")){
                Thread.sleep(100);//实际上应该是method.invoke(realObj, args)
            }
            return null;
        }
    }
    //返回实际代理对象实例
    public static final Connection createConnection(){
//        Object realConnection = new Object();
//        ConnectionHandler connectionHandler = new ConnectionHandler(realConnection);
//        return (Connection) Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class<?>[]{Connection.class},
//                connectionHandler);//返回了一个代理Connection实例
        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),
                new Class<?>[]{Connection.class}, new ConnectionHandler());
    }

}
