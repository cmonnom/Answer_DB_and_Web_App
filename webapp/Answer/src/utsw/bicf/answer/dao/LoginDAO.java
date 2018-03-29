package utsw.bicf.answer.dao;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utsw.bicf.answer.model.User;

@Repository
public class LoginDAO {

	//inject the session factory
	@Autowired
	private SessionFactory sessionFactory;
	
	public void closeUserSession(HttpSession session) {
		session.removeAttribute("user");
		
	}
	
	@Transactional
	public User getUserByUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from User where username = :username";
		Query<User> query = session.createQuery(hql.toString(), User.class).setParameter("username", username);
		User user = null;
		List<User> results = query.list();
		if (results != null && !results.isEmpty()) {
			user = results.get(0);
		}
		return user;
	}
}
