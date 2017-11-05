package com.abc.raja.usermgmt.bean;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class CharAmount {

	private String character;
	private Integer amount;

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("character", character).add("amount", amount).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(character, amount);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CharAmount) {
			CharAmount that = (CharAmount) object;
			return Objects.equal(this.character, that.character) && Objects.equal(this.amount, that.amount);
		}
		return false;
	}

}
