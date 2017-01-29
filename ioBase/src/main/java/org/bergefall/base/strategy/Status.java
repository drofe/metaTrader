package org.bergefall.base.strategy;

public class Status {

	public static final int OK = 0;
	public static final int ERROR = 10;
	private int code;
	private String message;
	
	public Status() {
		code = OK;
	}
	
	public Status(int code) {
		this.code = code;
	}
	
	public Status(int code, String msg) {
		this.code = code;
		this.message = msg;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public void setMsg(String msg) {
		this.message = msg;
	}
}

