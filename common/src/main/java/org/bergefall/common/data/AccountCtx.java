package org.bergefall.common.data;

public class AccountCtx {

	private String name;
	private long id;
	private String broker;
	private String user;
	
	public AccountCtx(String name, long id, String broker, String user) {
		this.name = name;
		this.id = id;
		this.broker = broker;
		this.user = user;
	}
	
	public String getName() {
		return name;
	}
	public long getId() {
		return id;
	}
	public String getBroker() {
		return broker;
	}
	public String getUser() {
		return user;
	}
	
}
