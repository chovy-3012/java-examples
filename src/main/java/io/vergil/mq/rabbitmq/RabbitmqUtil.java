package io.vergil.mq.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitmqUtil {
	private static Connection connection;

	// 获取连接
	public static Connection getConnection() {
		if (connection != null) {
			return connection;
		}
		String userName = "admin";
		String password = "admin";
		String host = "192.168.1.248";
		int port = 5672;
		boolean recoveryEnabled = true;
		// connection factory
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(userName);
		factory.setPassword(password);
		factory.setHost(host);
		factory.setPort(port);
		factory.setAutomaticRecoveryEnabled(recoveryEnabled);
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return connection;
	}

	// 获取通道
	public static Channel getChannel() {
		Channel channel = null;
		Connection connection2 = getConnection();
		try {
			channel = connection2.createChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return channel;
	}
}
