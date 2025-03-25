package com.ghf.server.rpcServerImpl;

import com.ghf.server.handler.SocketServerHandler;
import com.ghf.server.RPCServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolServer implements RPCServer {
    ThreadPoolExecutor threadPoolExecutor;
    @Override
    public void start(Integer port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            threadPoolExecutor = new ThreadPoolExecutor(5,
                    10,
                    60,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(100),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            while(true)
            {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(new SocketServerHandler(socket));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void stop() {

    }
}
