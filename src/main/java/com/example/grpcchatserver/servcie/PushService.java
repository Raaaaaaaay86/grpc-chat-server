package com.example.grpcchatserver.servcie;

import com.example.grpcchatserver.dto.IdentityStreamObserver;
import com.example.grpcchatserver.observer.ChannelObserver;
import com.example.push.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GrpcService
public class PushService extends PushServiceGrpc.PushServiceImplBase {

    @Autowired
    private ChannelObserver channelObserver;

    @Override
    public void chatRoomStream(ChatRoomStreamRequest request, StreamObserver<ChatRoomStreamReply> responseObserver) {
        channelObserver.subscribe(request.getChatroomID(), request.getUsername(), responseObserver);
    }

    @Override
    public void closeChatRoom(CloseChatRoomRequest request, StreamObserver<CloseChatRoomReply> responseObserver) {
        channelObserver.closeChannel(request.getChatroomID());

        var successMessage = Success.newBuilder()
                .setIsSuccess(true)
                .build();

        var reply = CloseChatRoomReply.newBuilder()
                .setSuccess(successMessage)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void unsubscribeStream(UnsubscribeStreamRequest request, StreamObserver<UnsubscribeStreamReply> responseObserver) {
        channelObserver.unsubscribe(request.getChatroomID(), request.getUsername());

        var successMessage = Success.newBuilder()
                .setIsSuccess(true)
                .build();

        var reply = UnsubscribeStreamReply.newBuilder()
                .setSuccess(successMessage)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }


}
