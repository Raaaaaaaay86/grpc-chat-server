package com.example.grpcchatserver.servcie;

import com.example.push.Message;
import com.example.push.MessagesReply;
import com.example.push.MessagesRequest;
import com.example.push.PushServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Date;

@GrpcService
public class PushService extends PushServiceGrpc.PushServiceImplBase {

    private static int runningId = 0;

    @Override
    public void messages(MessagesRequest request, StreamObserver<MessagesReply> responseObserver) {

        var message = Message.newBuilder()
                .setCreateTime(new Date().getTime())
                .setChatroomID(request.getChatroomID())
                .setUsername("KING")
                .setContent("Hello ANAN")
                .setId(Integer.toString(PushService.runningId))
                .build();

        PushService.runningId += 1;

        var reply = MessagesReply.newBuilder()
                        .setMessage(message)
                        .build();

        responseObserver.onNext(reply);
    }
}
