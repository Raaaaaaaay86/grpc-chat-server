package com.example.grpcchatserver.dto;

import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class IdentityStreamObserver<T> {
    private String username;
    private StreamObserver<T> streamObserver;

    public IdentityStreamObserver(StreamObserver<T> streamObserver, String username) {
        this.streamObserver = streamObserver;
        this.username = username;
    }

    public StreamObserver<T> getStreamObserver() {
        return streamObserver;
    }

    public String getUsername() {
        return username;
    }
}
