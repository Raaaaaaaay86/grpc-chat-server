package com.example.grpcchatserver;

import com.example.chatManager.ChatManagerServiceGrpc;
import com.example.chatManager.ChatServerStreamRequest;
import com.example.chatManager.ChatServerStreamResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class GrpcChatServerApplication {

	@GrpcClient("chat-manager")
	static ChatManagerServiceGrpc.ChatManagerServiceStub chatManagerServiceStub;

	@Value("${container.headless-service}")
	String K8S_HEADLESS_SERVICE;

	@Value("${container.namespace}")
	String K8S_NAMESPACE;

	@Value("${grpc.client.chat-manager.address}")
	String CHAT_MANAGER_ADDRESS;

	String K8S_HOSTNAME = InetAddress.getLocalHost().getHostName();

	Logger logger = LoggerFactory.getLogger(GrpcChatServerApplication.class);

	public GrpcChatServerApplication() throws UnknownHostException {
	}

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication.run(GrpcChatServerApplication.class, args);
	}

	@Bean(initMethod = "init")
	void streamWithChatManager() throws UnknownHostException {
		logger.info("Creating bidirectional gRPC connection to chat-server-manager: {}", CHAT_MANAGER_ADDRESS);

		StreamObserver<ChatServerStreamResponse> responseObserver = new StreamObserver<ChatServerStreamResponse>() {
			@Override
			public void onNext(ChatServerStreamResponse chatSeverStreamResponse) {
				logger.info(chatSeverStreamResponse.getMessage());
			}

			@Override
			public void onError(Throwable throwable) {
				logger.error("Got error when creating bidirectional gRPC connection to chat-server-manager: {}", throwable.getMessage());
			}

			@Override
			public void onCompleted() {
				logger.info("Connection has been completed by chat-manager");
			}
		};

		StreamObserver<ChatServerStreamRequest> requestObserver = chatManagerServiceStub.chatSeverStream(responseObserver);

		var headlessDNS = K8S_HOSTNAME + "." + K8S_HEADLESS_SERVICE + "." + K8S_NAMESPACE + ".svc.cluster.local";

		var request =  ChatServerStreamRequest.newBuilder()
						.setDns(headlessDNS)
						.setServerName(K8S_HOSTNAME)
						.build();

		requestObserver.onNext(request);
	}
}
