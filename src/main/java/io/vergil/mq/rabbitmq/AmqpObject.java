package io.vergil.mq.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AmqpObject {
	private ConnectionFactory connectionFactory;
	private Connection connection;

	public AmqpObject(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
		this.connectionFactory = connectionFactory;
		this.connection = connectionFactory.newConnection();
	}

	public AmqpObject(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @Description 关闭连接
	 * @Author zhaow(zhaow@htdatacloud.com)
	 * @Date 2016年5月25日
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * @Description 发送队列消息
	 * @Author zhaow(zhaow@htdatacloud.com)
	 * @Date 2016年5月25日
	 * @param message
	 *            消息对象
	 * @param queueName
	 *            队列名称
	 * @throws IOException
	 */
	public void produceQueueMessage(Object message, String queueName) throws IOException {
		Channel channel = connection.createChannel();
		channel.queueDeclare(queueName, true, true, true, null);
		String str = new Gson().toJson(message);
		byte[] messageBodyBytes = str.getBytes();
		channel.basicPublish("", queueName, null, messageBodyBytes);
		try {
			channel.close();
		} catch (TimeoutException e) {
		}
	}
	
	

	/**
	 * @Description 发送订阅消息
	 * @Author zhaow(zhaow@htdatacloud.com)
	 * @Date 2016年5月25日
	 * @param message
	 * @param topicName
	 * @throws IOException
	 */
	public void produceTopicMessage(Object message, String topicName) throws IOException {
		Channel channel = connection.createChannel();
		String str = new Gson().toJson(message);
		byte[] messageBodyBytes = str.getBytes();
		channel.basicPublish("amq.topic", topicName, null, messageBodyBytes);
		try {
			channel.close();
		} catch (TimeoutException e) {
		}
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
