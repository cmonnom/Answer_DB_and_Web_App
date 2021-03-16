package utsw.bicf.answer.dao;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utsw.bicf.answer.model.LoginAttempt;
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
	public void saveObject(Object object) {
		sessionFactory.getCurrentSession().saveOrUpdate(object);
	}
	
	@Transactional
	public <T> T getObject(Class<T> clazz, int id) {
		return sessionFactory.getCurrentSession().get(clazz, id);
	}

	@Transactional
	public void deleteObject(Object object) {
		sessionFactory.getCurrentSession().delete(object);
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

	@Transactional
	public User getUserByUsernameOrEmail(String usernameOrEmail) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from User where username = :usernameOrEmail or email = :usernameOrEmail";
		Query<User> query = session.createQuery(hql.toString(), User.class).setParameter("usernameOrEmail", usernameOrEmail);
		User user = null;
		List<User> results = query.list();
		if (results != null && !results.isEmpty()) {
			user = results.get(0);
			Hibernate.initialize(user.getGroups());
		}
		return user;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void resetAllVersion() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "update Version set isCurrent = false";
		Query<User> query = session.createQuery(hql.toString());
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateToVersion1() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "update Version set isCurrent = true where versionId = 2";
		Query<User> query = session.createQuery(hql.toString());
		query.executeUpdate();
	}
	
	@Transactional
	public LoginAttempt getLoginAttemptForUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		String hql = null;
		Query<LoginAttempt> query = null;
		if (user == null) {
			hql = "from LoginAttempt where user is null";
			query = session.createQuery(hql.toString(), LoginAttempt.class);
		}
		else {
			hql = "from LoginAttempt where user = :user";
			query = session.createQuery(hql.toString(), LoginAttempt.class).setParameter("user", user);
		}
		LoginAttempt loginAttempt = null;
		List<LoginAttempt> results = query.list();
		if (results != null && !results.isEmpty()) {
			loginAttempt = results.get(0);
		}
		return loginAttempt;
	}
	
	@Transactional
	public List<LoginAttempt> getAllLoginAttempt() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from LoginAttempt";
		return session.createQuery(hql.toString(), LoginAttempt.class).list();
	}
}
