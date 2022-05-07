package com.makotogroup.log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class User {
	private User(String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}
	private String userId;
	private String userName;
	public String getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Naive implementation. No angry emails, please.
	 * @return
	 */
	public synchronized static User getRandomUser() {
		Random r = new Random();
		return users.get(r.nextInt(users.size()));
	}
	
	public static User PAIGE = new User("U0001", "Paige");
	public static User MIKE = new User("U0002", "Mike");
	public static User BRENNAN = new User("U0003", "Brennan");
	public static User BILLY = new User("U0004", "Billy");
	public static User LORA = new User("U0005", "Lora");
	public static User JEREMY = new User("U0006", "Jeremy");
	public static User MADISON = new User("U0007", "Madison");
	public static User FOSTER = new User("U0008", "Foster");
	public static User LAYLA = new User("U0009", "Layla");
	
	private static List<User> users = new ArrayList<User>();
	static {
		users.add(PAIGE);
		users.add(MIKE);
		users.add(BRENNAN);
		users.add(BILLY);
		users.add(LORA);
		users.add(JEREMY);
		users.add(MADISON);
		users.add(FOSTER);
		users.add(LAYLA);
	}
}
