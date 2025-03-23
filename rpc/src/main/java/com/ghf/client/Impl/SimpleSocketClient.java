package com.ghf.client.Impl;

import com.ghf.client.RPCClient;
import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleSocketClient implements RPCClient {
    private String hostName;
    private Integer port;

    public SimpleSocketClient(String hostName, Integer port) {
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public RpcResponse sendRPCRequest( RpcRequest rpcRequest) {
        try{
            Socket socket = new Socket(hostName, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(rpcRequest);
            oos.flush();

            RpcResponse rpcResponse = (RpcResponse)ois.readObject();
            return rpcResponse;

        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
