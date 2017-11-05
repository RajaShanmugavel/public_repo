package com.abc.raja.usermgmt.dao;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@Entity
public class UserString {

	@Id
	private String userId;
	private String stringState;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStringState() {
		return stringState;
	}

	public void setStringState(String stringState) {
		this.stringState = stringState;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("userId", userId).add("stringState", stringState).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userId, stringState);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof UserString) {
			UserString that = (UserString) object;
			return Objects.equal(this.userId, that.userId) && Objects.equal(this.stringState, that.stringState);
		}
		return false;
	}

}
