package com.spring4.tr;

public class AfterThrowingAdvice {
	public AfterThrowingAdvice() {}
	public void exceptionMsg() {
		System.out.println("[예외처리] 비즈니스 로직 수행중 예외 발생");
	}
}
