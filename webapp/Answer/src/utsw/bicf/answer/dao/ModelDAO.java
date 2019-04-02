package utsw.bicf.answer.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.GeneToReport;
import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.HeaderConfig;
import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.Token;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.UserRank;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.Version;

@Repository
public class ModelDAO {

	// inject the session factory
	@Autowired
	private SessionFactory sessionFactory;
	
	// @Transactional
	// public DnaExtract getDnaExtractByLimsId(String limsId) {
	// Session session = sessionFactory.getCurrentSession();
	// String hql = "from DnaExtract where limsId = :limsId";
	// List<DnaExtract> dnaExtracts = session.createQuery(hql,
	// DnaExtract.class).setParameter("limsId", limsId).list();
	// if (dnaExtracts != null && dnaExtracts.size() == 1) {
	// return dnaExtracts.get(0);
	// }
	// return null;
	// }

//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Transactional
//	public List<Annotation> getAnnotationsForOrganisationAndUser(String origin, User user, Gene gene) {
//		Session session = sessionFactory.getCurrentSession();
//		StringBuilder sql = new StringBuilder("select * from annotation where origin = :origin ")
//				.append(" and gene_id = :geneId ").append(" and !deleted ");
//		if (user != null) {
//			sql.append(" and answer_user_id = :user ");
//		}
//		Query query = session.createNativeQuery(sql.toString(), Annotation.class)
//				.setParameter("geneId", gene.getGeneId()).setParameter("origin", origin);
//		if (user != null) {
//			query.setParameter("user", user);
//		}
//		return query.list();
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Transactional
//	public Gene getGeneBySymbol(String symbol) {
//		Session session = sessionFactory.getCurrentSession();
//		StringBuilder sql = new StringBuilder("select * from gene where symbol = :symbol ");
//		Query query = session.createNativeQuery(sql.toString(), Gene.class).setParameter("symbol", symbol);
//		List<Gene> genes = query.list();
//		if (genes != null && genes.size() == 1) {
//			return genes.get(0);
//		}
//		return null;
//	}

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

//	@SuppressWarnings("unchecked")
//	@Transactional
//	public VariantSelected getVariantSelectedByGeneVariantAndCase(String geneAndVariant, OrderCase orderCase) {
//		Session session = sessionFactory.getCurrentSession();
//		StringBuilder hql = new StringBuilder(
//				"from VariantSelected where geneAndVariant = :geneAndVariant and orderCase = :orderCase ");
//		Query query = session.createQuery(hql.toString(), VariantSelected.class)
//				.setParameter("geneAndVariant", geneAndVariant)
//				.setParameter("orderCase", orderCase);
//		List<VariantSelected> variants = query.list();
//		if (variants != null && variants.size() == 1) {
//			return variants.get(0);
//		}
//		return null;
//	}
	
//	@SuppressWarnings("unchecked")
//	@Transactional
//	public List<VariantSelected> getAllVariantsSelectedByCase(OrderCase orderCase) {
//		Session session = sessionFactory.getCurrentSession();
//		StringBuilder hql = new StringBuilder(
//				"from VariantSelected where orderCase = :orderCase ");
//		Query query = session.createQuery(hql.toString(), VariantSelected.class)
//				.setParameter("orderCase", orderCase);
//		return query.list();
//	}

	@Transactional
	public Token getParseMDAToken(String token) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Token where type = 'parse-mda' and token = :token";
		Token theToken = session.createQuery(hql, Token.class).setParameter("token", token).uniqueResult();
		return theToken;
	}

	@Transactional
	public Token getAPIToken() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Token where type = 'parse-mda'";
		Token theToken = session.createQuery(hql, Token.class).uniqueResult();
		return theToken;
	}

	@Transactional
	public AnswerDBCredentials getAnswerDBCredentials() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from AnswerDBCredentials";
		AnswerDBCredentials db = session.createQuery(hql, AnswerDBCredentials.class).uniqueResult();
		return db;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional
	public List<User> getAllUsers() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from User order by last";
		List<User> users = session.createQuery(hql, User.class).list();
		users.stream().forEach(u -> Hibernate.initialize(u.getGroups()));
		return users;
	}
	
	@Transactional
	public List<Group> getAllGroups() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Group";
		List<Group> groups = session.createQuery(hql, Group.class).list();
		groups.stream().forEach(g -> Hibernate.initialize(g.getUsers()));
		return groups;
	}
	
	@Transactional
	public User getUserByUserId(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from User where userId = :userId";
		User user = session.createQuery(hql, User.class).setParameter("userId", userId).uniqueResult();
		if (user != null) {
			Hibernate.initialize(user.getGroups());
		}
		return user;
	}
	
	@Transactional
	public Group getGroupByGroupId(Integer groupId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Group where groupId = :groupId";
		Group group = session.createQuery(hql, Group.class).setParameter("groupId", groupId).uniqueResult();
		if (group != null) {
			Hibernate.initialize(group.getUsers());
		}
		return group;
	}
	
	@Transactional
	public ReportGroup getReportGroupById(Integer reportGroupId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from ReportGroup where reportGroupId = :reportGroupId";
		return session.createQuery(hql, ReportGroup.class).setParameter("reportGroupId", reportGroupId).uniqueResult();
	}
	
	@Transactional
	public Version getCurrentVersion() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Version where isCurrent = true";
		return session.createQuery(hql, Version.class).uniqueResult();
	}

//	/**
//	 * Permissions should be from finalized to edit to view
//	 * If finalize is true, edit and view should be true
//	 * If edit is true, view should be true
//	 * @param view
//	 * @param edit
//	 * @param finalize
//	 * @param admin
//	 * @return
//	 */
//	@Transactional
//	public Permission getPermission(Boolean view, Boolean edit, Boolean finalize, Boolean admin) {
//		Session session = sessionFactory.getCurrentSession();
//		String name = null;
//		if (admin) {
//			name = "admin";
//		}
//		else if (finalize) {
//			name = "view_edit_finalize";
//		}
//		else if (edit) {
//			name = "view_edit";
//		}
//		else if (view) {
//			name = "view_only";
//		}
//		else {
//			name = "disabled";
//		}
//		String hql = "from Permission where name = :name";
//		return session.createQuery(hql, Permission.class)
//				.setParameter("name", name).uniqueResult();
//	}

	@Transactional
	public List<VariantFilterList> getVariantFilterListsForUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from VariantFilterList where user = :user";
		return session.createQuery(hql, VariantFilterList.class)
				.setParameter("user", user).list();
	}

	@Transactional
	public List<ReportGroup> getAllReportGroups() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from ReportGroup";
		return session.createQuery(hql, ReportGroup.class).list();
	}
	
	@Transactional
	public List<GeneToReport> getAllGenesToReportInReportGroup(ReportGroup reportGroup) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "select * from gene_to_report where report_group_id = :reportGroupId";
		return session.createNativeQuery(sql, GeneToReport.class).
				setParameter("reportGroupId", reportGroup.getReportGroupId())
				.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> getGenesInPanels(List<String> cleanGenes) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT gene_name FROM gene_to_report where gene_name in :cleanGenes";
		return session.createNativeQuery(sql).
				setParameter("cleanGenes", cleanGenes)
				.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> getAllGenesInPanels() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT distinct(gene_name) FROM gene_to_report order by gene_name";
		return session.createNativeQuery(sql)
				.list();
	}

	@Transactional
	public UserRank getFirstRank() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT * FROM user_rank order by level";
		return session.createNativeQuery(sql, UserRank.class)
				.list().get(0);
	}

	@Transactional
	public List<User> getAdmins() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT u.* FROM answer_user u, individual_permission i" 
		+" where u.individual_permission_id = i.individual_permission_id "
				+ " and i.admin is true";
		return session.createNativeQuery(sql, User.class).list();
	}

//	@Transactional
//	public OrderCase getOrderCaseByEpicOrderNumber(Integer epicOrderNumber) {
//		Session session = sessionFactory.getCurrentSession();
//		String hql = "from OrderCase where epicOrderNumber = :epicOrderNumber";
//		List<OrderCase> cases = session.createQuery(hql, OrderCase.class).list();
//		if (cases != null && !cases.isEmpty()) {
//			return cases.get(0);
//		}
//		return null;
//		
//	}
	
	@Transactional
	public List<HeaderConfig> getHeaderConfigForUserAndTable(Integer userId, String tableTitle) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT * FROM header_config" 
		+" where answer_user_id = :userId "
				+ " and table_title = :tableTitle";
		return session.createNativeQuery(sql, HeaderConfig.class)
				.setParameter("userId", userId)
				.setParameter("tableTitle", tableTitle)
				.list();
	}

	@Transactional
	public List<HeaderConfig> getAllHeaderConfigForUser(User user) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT * FROM header_config" 
		+" where answer_user_id = :userId ";
		return session.createNativeQuery(sql, HeaderConfig.class)
				.setParameter("userId", user.getUserId())
				.list();
	}


	
	
	
}
