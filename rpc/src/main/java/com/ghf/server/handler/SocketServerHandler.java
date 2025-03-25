package com.ghf.server.handler;

import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;
import com.ghf.server.register.LocalRegister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SocketServerHandler implements Runnable{
    private Socket socket;
    public SocketServerHandler(Socket socket)
    {
        this.socket = socket;
    }
    @Override
    public void run() {

        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());;
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());;

            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            RpcResponse rpcResponse = handle(rpcRequest);

            oos.writeObject(rpcResponse);
            oos.flush();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static RpcResponse handle(RpcRequest rpcRequest)
    {
        try {
            Class clazz = LocalRegister.get(rpcRequest.getInterfaceName());
            Method method = clazz.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
            Object result = method.invoke(clazz.newInstance(),rpcRequest.getParameters());
            return RpcResponse.success(result);

        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return RpcResponse.fail();
        }

    }
}
