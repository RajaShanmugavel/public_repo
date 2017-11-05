package com.abc.raja.usermgmt.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.abc.raja.usermgmt.dao.UserString;

@Component
@Scope("singleton")
public class UserBean {
	static UserString userString = null;

	public UserString newUser() {
		if (userString == null) {
			userString = new UserString();
		}
		return userString;
	}

}
