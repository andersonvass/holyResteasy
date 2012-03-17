package org.holy.holyResteasy.user.domain;

import org.holy.holyResteasy.general.domain.BaseEntity;

public class User extends BaseEntity {

	private static final long serialVersionUID = 3048695542503465063L;

	private String login;

	private String password;

	private String name;

	private boolean active;

	private byte[] photo;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

}
