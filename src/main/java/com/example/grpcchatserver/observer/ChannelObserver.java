package com.example.grpcchatserver.observer;

import com.example.grpcchatserver.dto.IdentityStreamObserver;
import com.example.push.ChatRoomStreamReply;
import com.example.push.PushMessage;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class ChannelObserver {
    private static final Map<String, List<IdentityStreamObserver<ChatRoomStreamReply>>> subscribers = new HashMap<>();

    public void subscribe(String channelId, String username, StreamObserver<ChatRoomStreamReply> streamObserver) {
        if (!subscribers.containsKey(channelId)) {
            var list = new LinkedList<IdentityStreamObserver<ChatRoomStreamReply>>();
            subscribers.put(channelId, list);
        }

        var subscriberList = subscribers.get(channelId);
        subscriberList.add(new IdentityStreamObserver<>(streamObserver, username));
    }

    public void unsubscribe(String channelId, String observerId) {
        if (!subscribers.containsKey(channelId)) {
            return;
        }

        var subscriberList = subscribers.get(channelId);

        var runningIndex = 0;
        for (IdentityStreamObserver<ChatRoomStreamReply> identityStreamObserver : subscriberList) {
            if (identityStreamObserver.getUsername().equals(observerId)) {
                identityStreamObserver.getStreamObserver().onCompleted();
                subscriberList.remove(runningIndex);
                break;
            }

            runningIndex += 1;
        }
    }

    public void closeChannel(String channelId) {
        if (!subscribers.containsKey(channelId)) {
            return;
        }

        subscribers.get(channelId).forEach(identityStreamObserver -> {
            identityStreamObserver.getStreamObserver().onCompleted();
        });

        subscribers.remove(channelId);
    }

    public void broadcast(String channelId, PushMessage pushMessage) {
        if (!subscribers.containsKey(channelId)) {
            return;
        }

        var subscriberList = subscribers.get(channelId);
        subscriberList.forEach(identityStreamObserver -> {
            var reply = ChatRoomStreamReply
                    .newBuilder()
                    .setMessage(pushMessage)
                    .build();

            identityStreamObserver.getStreamObserver().onNext(reply);
        });
    }
}
