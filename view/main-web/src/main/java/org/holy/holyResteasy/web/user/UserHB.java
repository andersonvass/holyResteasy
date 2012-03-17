package org.holy.holyResteasy.web.user;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.holy.holyResteasy.common.rest.JSONEncodingObject;
import org.holy.holyResteasy.user.domain.User;

@Path("/users")
public class UserHB {

	public UserHB() {
	}

	@GET
	@Path("/logado")
	public String usuarioLogado() {
		User loggedUser = new User();
		return new JSONEncodingObject(loggedUser).toJson();
	}

	@POST
	@Path("/searchUsers")
	public String searchUsers() {
		List<User> users = createUsers(200);
		String json = new JSONEncodingObject(users).toJson();
		return json;
	}

	private List<User> createUsers(int numberOfUsers) {
		List<User> users = new ArrayList<User>();
		for (int i = 0; i < numberOfUsers; i++) {
			users.add(createUser());
		}
		return users;
	}

	private User createUser() {
		User user = new User();
		user.setName("User_" + System.currentTimeMillis());
		user.setLogin("Login_" + System.currentTimeMillis());
		return user;
	}

}
