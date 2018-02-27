package io.vergil.mq.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

//发布订阅模式
public class Test4 {
	private String queueName = "testQueue";
	private String queueName1 = "testQueue1";
	private String exchange = "testExchange2";

	public void produce() {
		Channel channel = null;
		try {
			// 创建通道
			channel = RabbitmqUtil.getChannel();
			// 创建两个队列接收订阅消息
			channel.queueDeclare(queueName, true, false, false, null);
			channel.queueDeclare(queueName1, true, false, false, null);
			// 创建一个exchange
			channel.exchangeDeclare(exchange, "topic");
			// 给两个队列指定对应的订阅内容
			channel.queueBind(queueName, exchange, "#.2.#");
			channel.queueBind(queueName1, exchange, "#.2.?");
			for (;;) {
				// 发送两个routingkey消息内容
				// 发送routingkey “my test1”,匹配两个队列
				channel.basicPublish(exchange, "2.11", null, "test1".getBytes());
				System.out.println("生产者：test1");
				// 发送routingkey “my test2”,去掉后面的11，两个队列都能接收到
				channel.basicPublish(exchange, "2.13.11", null, "test2".getBytes());
				System.out.println("生产者：test2");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
	}

	public void consume() {
		Channel channel = null;
		try {
			channel = RabbitmqUtil.getConnection().createChannel();
			DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					super.handleDelivery(consumerTag, envelope, properties, body);
					String message = new String(body, "UTF-8");
					System.out.println(" 接收消息1:'" + message + "'");
				}

			};
			channel.basicConsume(queueName, true, defaultConsumer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void consume2() {
		Channel channel = null;
		try {
			channel = RabbitmqUtil.getConnection().createChannel();
			DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					super.handleDelivery(consumerTag, envelope, properties, body);
					String message = new String(body, "UTF-8");
					System.out.println(" 接收消息2:'" + message + "'");
				}

			};
			channel.basicConsume(queueName1, true, defaultConsumer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void main(String[] args) {
		final Test4 test1 = new Test4();
		new Thread() {
			@Override
			public void run() {
				test1.produce();
			}

		}.start();
		new Thread() {
			@Override
			public void run() {
				test1.consume();
			}

		}.start();
		new Thread() {
			@Override
			public void run() {
				test1.consume2();
			}

		}.start();
	}
}
