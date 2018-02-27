package io.vergil.mq.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

//简单例子
public class Test1 {
	private String queueName = "testQueue";
	private String routingKey = "routingKey";
	private String exchange = "testExchange";

	public void produce() {
		Channel channel = null;
		try {
			// 创建通道
			channel = RabbitmqUtil.getChannel();
			// 创建队列,持久、非专用、非自动删除的队列
			channel.queueDeclare(queueName, true, false, false, null);
			// 创建一个exchange，使用rabbitmq内置默认exchange也可以,默认的exchange是""一个direct类型
			channel.exchangeDeclare(exchange, "direct");
			// 使用routing key绑定exchange和queue
			channel.queueBind(queueName, exchange, routingKey);
			for (;;) {
				channel.basicPublish(exchange, routingKey, null, "test".getBytes());
				System.out.println("生产者：test");
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
					System.out.println(" 接收消息:'" + message + "'");
				}

			};
			channel.basicConsume(queueName, true, defaultConsumer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void main(String[] args) {
		final Test1 test1 = new Test1();
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
	}
}
