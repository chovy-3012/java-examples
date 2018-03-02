package io.vergil.wys;

public class WyxTest {
	public static void main(String[] args) {
		System.out.println(16 >> 2);
		System.out.println(-16 >> 2);
		// 16二进制  00000000 00000000 00000000 00010000
		// -16二进制11111111 11111111 11111111 11110000
		// 右移两位   00111111 11111111 11111111 11111100
		System.out.println(-16 >>> 2);
		System.out.println(Integer.parseInt("00111111111111111111111111111100",2));
	}
}
