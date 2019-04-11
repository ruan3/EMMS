package com.esquel.epass.schema;

public enum SystemApplication {
	
	EPAY_SLIP(1), TASK(2), LEAVE(3), APP_STORE(4);
	
	SystemApplication(int identifier) {
		id = identifier;
	}
	int id;
	
	public int getId() {
		return id;
	}
}
