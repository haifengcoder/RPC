package com.ghf.client;

import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;

public interface RPCClient {
    public RpcResponse sendRPCRequest(RpcRequest request) throws InterruptedException;
}
