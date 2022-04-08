package com.example.grpcchatserver.servcie;
import com.example.grpcchatserver.observer.ChannelObserver;
import com.example.message.Message;
import com.example.message.MessageServiceGrpc;
import com.example.message.SendMessageReply;
import com.example.message.SendMessageRequest;
import com.example.push.PushMessage;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@GrpcService
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {

    private static int runningId = 0;

    @Autowired
    ChannelObserver channelObserver;

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageReply> responseObserver) {
        var createTime = new Date().getTime();

        var message = Message.newBuilder()
                .setId(Integer.toString(MessageService.runningId))
                .setCreateTime(createTime)
                .setChatroomID(request.getChatroomID())
                .setUsername(request.getUsername())
                .setContent(request.getContent())
                .build();

        SendMessageReply reply = SendMessageReply.newBuilder()
                        .setMessage(message)
                        .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();

        // Broadcast message to channel subscribers
        var pushMessage= PushMessage.newBuilder()
                .setId(Integer.toString(runningId))
                .setCreateTime(new Date().getTime())
                .setUsername(request.getUsername())
                .setContent(request.getContent())
                .setChatroomID(request.getChatroomID())
                .build();
        channelObserver.broadcast(request.getChatroomID(), pushMessage);

        MessageService.runningId += 1;
    }
}
