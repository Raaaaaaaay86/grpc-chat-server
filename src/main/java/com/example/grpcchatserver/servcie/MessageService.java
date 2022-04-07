package com.example.grpcchatserver.servcie;
import com.example.message.Message;
import com.example.message.MessageServiceGrpc;
import com.example.message.SendMessageReply;
import com.example.message.SendMessageRequest;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Date;

@GrpcService
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {

    private static int runningId = 0;

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageReply> responseObserver) {

        var message = Message.newBuilder()
                .setCreateTime(new Date().getTime())
                .setChatroomID(request.getChatroomID())
                .setUsername(request.getUsername())
                .setContent("Hello " + request.getContent())
                .setId(Integer.toString(MessageService.runningId))
                .build();

        MessageService.runningId += 1;

        SendMessageReply reply = SendMessageReply.newBuilder()
                        .setMessage(message)
                        .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
