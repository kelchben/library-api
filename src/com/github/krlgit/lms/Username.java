package com.github.krlgit.lms;

// TODO unnecessary, memory eating Class? Change to String usernames?
class Username {
	private final String username;

	Username(String str) {
		this.username = str;
	}

	@Override
	public String toString() {
		return username; // TODO defensive copy more secure?
	}

	@Override
	public boolean equals(Object obj) {
		return obj.equals(username);
	}
 
	@Override
	public int hashCode() {
		return username.hashCode();
	}

}
