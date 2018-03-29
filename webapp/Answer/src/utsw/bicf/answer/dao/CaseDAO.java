package utsw.bicf.answer.dao;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utsw.bicf.answer.model.OrderCase;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.CurrentCaseUser;
import utsw.bicf.answer.model.MDAEmail;

@Repository
public class CaseDAO {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public OrderCase getCaseByPatientMRN(String mrn) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from OrderCase where patientMrn = :mrn";
		List<OrderCase> cases = session.createQuery(hql, OrderCase.class).setParameter("mrn", mrn).list();
		if (cases != null && cases.size() == 1) {
			return cases.get(0); //TODO only one case per patient? Should be.
		}
		return null;
	}
	
	@Transactional
	public MDAEmail createMDAEmail(File mdaFileOutput) throws IOException {
		Session currentSession = sessionFactory.getCurrentSession();
		//save file to database and start a new case
		MDAEmail mdaEmail = new MDAEmail();
		mdaEmail.setDateImported(LocalDateTime.now());
		mdaEmail.setFilename(mdaFileOutput.getName());
		mdaEmail.setRawHTML(FileUtils.readFileToString(mdaFileOutput, "UTF-8"));
		currentSession.saveOrUpdate(mdaEmail);
		return mdaEmail;
	}

	@Transactional
	public CurrentCaseUser getCurrentCaseUserByUserAndCase(OrderCase aCase, User user) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from CurrentCaseUser where user = :user and orderCase = :aCase ";
		List<CurrentCaseUser> cases = session.createQuery(hql, CurrentCaseUser.class)
				.setParameter("user", user)
				.setParameter("aCase", aCase)
				.list();
		if (cases != null && cases.size() == 1) {
			return cases.get(0); //TODO only one case per patient? Should be.
		}
		return null;
	}
	
	@Transactional
	public CurrentCaseUser getCurrentCaseUserByUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from CurrentCaseUser where user = :user ";
		List<CurrentCaseUser> cases = session.createQuery(hql, CurrentCaseUser.class)
				.setParameter("user", user)
				.list();
		if (cases != null && cases.size() == 1) {
			return cases.get(0); //TODO only one case per patient? Should be.
		}
		return null;
	}

}
