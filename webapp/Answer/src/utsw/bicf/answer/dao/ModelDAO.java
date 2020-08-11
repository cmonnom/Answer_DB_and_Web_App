package utsw.bicf.answer.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utsw.bicf.answer.controller.serialization.plotly.Trace;
import utsw.bicf.answer.db.api.utils.LookupUtils;
import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.ClinicalTest;
import utsw.bicf.answer.model.CosmicFusion;
import utsw.bicf.answer.model.GeneToReport;
import utsw.bicf.answer.model.GenieFusionCount;
import utsw.bicf.answer.model.GenieMutation;
import utsw.bicf.answer.model.GenieSample;
import utsw.bicf.answer.model.GenieSummary;
import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.HeaderConfig;
import utsw.bicf.answer.model.LookupVersion;
import utsw.bicf.answer.model.MSKHotspot;
import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.ResetToken;
import utsw.bicf.answer.model.Token;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.UserRank;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.Version;
import utsw.bicf.answer.model.extmapping.AminoAcid;
import utsw.bicf.answer.model.hybrid.CondonDistribution;
import utsw.bicf.answer.model.hybrid.GenericBarPlotData;
import utsw.bicf.answer.model.hybrid.GenericLollipopPlotData;
import utsw.bicf.answer.model.hybrid.GenericStackedBarPlotData2;
import utsw.bicf.answer.model.hybrid.RatioBarPlotData;

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
	public void saveBatch(List<Object> objects) {
		for (Object o : objects) {
			sessionFactory.getCurrentSession().saveOrUpdate(o);
		}
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
	public void deleteGenieTables() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "delete from genie_mutation";
		session.createNativeQuery(sql).executeUpdate();
		sql = "delete from genie_cna";
		session.createNativeQuery(sql).executeUpdate();
//		String
		sql = "delete from genie_fusion";
		session.createNativeQuery(sql).executeUpdate();
		sql = "delete from genie_sample";
		session.createNativeQuery(sql).executeUpdate();
		sql = "delete from genie_cna_count";
		session.createNativeQuery(sql).executeUpdate();
		sql = "delete from genie_fusion_count";
		session.createNativeQuery(sql).executeUpdate();
		sql = "delete from genie_summary";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_mutation AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_sample AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_summary AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_cna AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_cna_count AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_fusion AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE genie_fusion_count AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
	}
	
	@Transactional
	public void deleteCosmicTables() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "delete from cosmic_sample_fusion";
		session.createNativeQuery(sql).executeUpdate();
		sql = "delete from cosmic_fusion";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE cosmic_sample_fusion AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
		sql = "ALTER TABLE cosmic_fusion AUTO_INCREMENT = 1";
		session.createNativeQuery(sql).executeUpdate();
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
	public Token getUpdateGenieDataToken(String token) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Token where type = 'update-genie' and token = :token";
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
	public Token getDevLoginToken() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from Token where type = 'dev-login'";
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
	
	@Transactional
	public LookupVersion getLookupVersion(String databaseName) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from LookupVersion where databaseName = :databaseName";
		LookupVersion db = session.createQuery(hql, LookupVersion.class)
				.setParameter("databaseName", databaseName)
				.uniqueResult();
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
	public List<LookupVersion> getAllLookupVersions() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from LookupVersion";
		return session.createQuery(hql, LookupVersion.class).list();
	}
	
	@Transactional
	public Map<Integer, User> getAllUsersAsMap() {
		Map<Integer, User> usersById = new HashMap<Integer, User>();
		List<User> users = this.getAllUsers();
		for (User u : users) {
			usersById.put(u.getUserId(), u);
		}
		return usersById;
	}
	
	@Transactional
	public Map<String, LookupVersion> getAllLookupVersionsAsMap() {
		Map<String, LookupVersion> versionByDatabaseName = new HashMap<String, LookupVersion>();
		List<LookupVersion> versions = this.getAllLookupVersions();
		for (LookupVersion v : versions) {
			versionByDatabaseName.put(v.getDatabaseName(), v);
		}
		return versionByDatabaseName;
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
	public User getUserByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from User where email = :email";
		User user = session.createQuery(hql, User.class).setParameter("email", email).uniqueResult();
		return user;
	}
	
	@Transactional
	public ResetToken getResetTokenByTokenValue(String token) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from ResetToken where token = :token";
		ResetToken resetToken = session.createQuery(hql, ResetToken.class).setParameter("token", token).uniqueResult();
		return resetToken;
	}
	
	@Transactional
	public ResetToken getResetTokenByTokenAndEmail(String token, String email) {
		Session session = sessionFactory.getCurrentSession();
		User user = this.getUserByEmail(email);
		if (user == null) {
			return null;
		}
		String hql = "from ResetToken where user = :user and token = :token";
		ResetToken resetToken = session.createQuery(hql, ResetToken.class)
				.setParameter("user", user)
				.setParameter("token", token)
				.uniqueResult();
		return resetToken;
	}
	
	@Transactional
	public void clearResetTokens() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "select * FROM reset_token where date_created < now() - interval 10 minute;";
		List<ResetToken> resetTokens = session.createNativeQuery(sql, ResetToken.class).list();
		if (resetTokens != null) {
			for (ResetToken token : resetTokens) {
				this.deleteObject(token);
			}
		}
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
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> searchGenesInPanels(String search) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT distinct(gene_name) FROM gene_to_report"
				+ " where gene_name like upper(:search) order by gene_name";
		return session.createNativeQuery(sql).setParameter("search", "%" + search + "%")
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

	@Transactional
	/**
	 * Quick and dirty bypass of PubMed API until we know what's going on
	 * @return
	 */
	public String getPubmedContent() {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT xml_content FROM temp_pubmed" 
		+" where temp_pubmed_id = 1 ";
		return (String) session.createNativeQuery(sql)
				.uniqueResult();
	}
	
	@Transactional
	public Map<String, Integer> getAllGenieSampleIdByTumorBarcode() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		Session session = sessionFactory.getCurrentSession();
		String hql = "from GenieSample";
		List<GenieSample> samples = session.createQuery(hql, GenieSample.class).list();
		for (GenieSample s : samples) {
			result.put(s.getSampleId(), s.getGenieSampleId());
		}
		return result;
	}
	
	@Transactional
	public Map<String, GenieSample> getAllGenieSamplesByTumorBarcode() {
		Map<String, GenieSample> result = new HashMap<String, GenieSample>();
		Session session = sessionFactory.getCurrentSession();
		String hql = "from GenieSample";
		List<GenieSample> samples = session.createQuery(hql, GenieSample.class).list();
		for (GenieSample s : samples) {
			result.put(s.getSampleId(), s);
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<GenericBarPlotData> getGenieVariantCountForGene(String hugoSymbol) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ( ")
		.append("select count(gs.cancer_type) as x, gs.cancer_type as y from genie_mutation gm, genie_sample gs ")
		.append("where gm.genie_sample_id = gs.genie_sample_id ")
		.append("and gm.hugo_symbol = :hugoSymbol ")
		.append("group by gs.cancer_type) as sub ")
		.append("order by x desc limit 10; ");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("hugoSymbol", hugoSymbol);
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<GenericBarPlotData> getGeniePatientCountPerCancer() {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select sum(count_cancer_type) x, cancer_type y from ")
		.append(" (select count(gs.cancer_type) as count_cancer_type, gs.cancer_type from genie_sample gs")
		.append(" group by patient_id, cancer_type) as sub")
		.append(" group by cancer_type")
		.append(" order by x desc limit 10;");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString());
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
	@Transactional
	public List<GenieSummary> getGenieSummary(String category) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from GenieSummary where category = :category";
		return session.createQuery(hql, GenieSummary.class).setParameter("category", category).list();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<GenericBarPlotData> getGeniePatientCountForGene(String hugoSymbol, List<String> cancers) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select count(cancer_type) as x, cancer_type as y ")
		.append(" from ( ")
		.append(" select gs.cancer_type from genie_mutation gm, genie_sample gs ")
				.append(" where gm.genie_sample_id = gs.genie_sample_id  ")
				.append(" and gm.hugo_symbol = :hugoSymbol ")
				.append(" and gs.cancer_type in :cancers ")
				.append(" group by gs.patient_id, gs.cancer_type) as sub ")
				.append(" group by cancer_type ");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("hugoSymbol", hugoSymbol)
				.setParameter("cancers", cancers);
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
	@SuppressWarnings({"deprecation", "unchecked" })
	@Transactional
	public List<GenericLollipopPlotData> getGeniePatientCountForGene(String hugoSymbol) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select")
		.append(" count(amino_acid_notation) as y, ")
		.append(" amino_acid_notation as label1,  ")
		.append(" group_concat(variant_change separator '/') as label2,  ")
		.append(" amino_acid_position as x ")
		.append(" from genie_mutation ")
		.append(" where hugo_symbol = :hugoSymbol and amino_acid_position is not null")
		.append(" group by amino_acid_notation, amino_acid_position ")
		.append(" order by amino_acid_position ");
		Query<GenericLollipopPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("hugoSymbol", hugoSymbol);
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericLollipopPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
//	@SuppressWarnings({ "unchecked", "deprecation" })
//	@Transactional
//	public List<GenericStackedBarPlotData> getMutatedGenesPerCancer(Set<String> oncotreeCodes) {
//		Session session = sessionFactory.getCurrentSession();
//		StringBuilder sb = new StringBuilder();
//		sb.append("select sum(x) total, group_concat(x) bar_x, group_concat(x1) cat_x, y from ( ")
//		.append(" select count(gm.hugo_symbol) as x, gm.variant_type x1, gm.hugo_symbol as y from genie_mutation gm, genie_sample gs ") 
//		.append(" where gm.genie_sample_id = gs.genie_sample_id ")
//		.append(" and gs.oncotree_code in :oncotreeCodes ")
//		.append(" group by gm.hugo_symbol, gm.variant_type) as sub ")
//		.append(" group by y")
//		.append(" order by total desc limit 10; ");
//		Query<GenericStackedBarPlotData> query = session.createNativeQuery(sb.toString())
//				.setParameterList("oncotreeCodes", oncotreeCodes);
//		
//		return query.setResultTransformer(new ResultTransformer() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Object transformTuple(Object[] values, String[] labels) {
//				return new GenericStackedBarPlotData(values, labels);
//			}
//
//			@SuppressWarnings("rawtypes")
//			@Override
//			public List transformList(List arg0) {
//				return arg0;
//			}
//		}).list();
//	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public GenericStackedBarPlotData2 getMutatedGenesPerCancer(Set<String> oncotreeCodes) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append(" select count(gm.hugo_symbol) as x, gm.hugo_symbol as y from genie_mutation gm, genie_sample gs ") 
		.append(" where gm.genie_sample_id = gs.genie_sample_id ")
		.append(" and gs.oncotree_code in :oncotreeCodes ")
		.append(" group by gm.hugo_symbol ");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameterList("oncotreeCodes", oncotreeCodes);
		
		List<GenericBarPlotData> mutationData = query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
		Map<String, Number> mutationsByGene = new HashMap<String, Number>();
		for (GenericBarPlotData m : mutationData) {
			mutationsByGene.put(m.getY(), m.getX());
		}
		
		sb = new StringBuilder();
		sb.append(" select count(gm.hugo_symbol) as x, gm.hugo_symbol as y from genie_cna gm, genie_sample gs ") 
		.append(" where gm.genie_sample_id = gs.genie_sample_id ")
		.append(" and gs.oncotree_code in :oncotreeCodes ")
		.append(" and gm.cna_value > 0 ")
		.append(" group by gm.hugo_symbol ");
		query = session.createNativeQuery(sb.toString())
				.setParameterList("oncotreeCodes", oncotreeCodes);
		
		List<GenericBarPlotData> cnaAmpData = query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
		Map<String, Number> cnasAmpByGene = new HashMap<String, Number>();
		for (GenericBarPlotData c : cnaAmpData) {
			cnasAmpByGene.put(c.getY(), c.getX());
		}
		
		sb = new StringBuilder();
		sb.append(" select count(gm.hugo_symbol) as x, gm.hugo_symbol as y from genie_cna gm, genie_sample gs ") 
		.append(" where gm.genie_sample_id = gs.genie_sample_id ")
		.append(" and gs.oncotree_code in :oncotreeCodes ")
		.append(" and gm.cna_value < 0 ")
		.append(" group by gm.hugo_symbol ");
		query = session.createNativeQuery(sb.toString())
				.setParameterList("oncotreeCodes", oncotreeCodes);
		
		List<GenericBarPlotData> cnaDelData = query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
		Map<String, Number> cnasDelByGene = new HashMap<String, Number>();
		for (GenericBarPlotData c : cnaDelData) {
			cnasDelByGene.put(c.getY(), c.getX());
		}
		
		GenericStackedBarPlotData2 stackedData = new GenericStackedBarPlotData2(mutationsByGene, cnasAmpByGene, cnasDelByGene);
		return stackedData;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<Trace> getCodonDiseaseDistribution(String geneTerm, String aminoAcidNotation) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ( select count(gm.variant_change) x, gm.variant_change y from genie_mutation gm, genie_sample gs ")
				.append(" where gm.hugo_symbol = :geneTerm and gm.amino_acid_notation = :aminoAcidNotation ")
				.append(" and gm.genie_sample_id = gs.genie_sample_id ")
				.append(" group by gm.variant_change ")
				.append(" order by x desc ) sub")
				.append(" where x > 2;");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("geneTerm", geneTerm)
				.setParameter("aminoAcidNotation", aminoAcidNotation);
		
		List<GenericBarPlotData> yData = query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
		Collections.reverse(yData);
		
		//top 9 cancers
		sb = new StringBuilder();
		sb.append("select count(gs.cancer_type) x, gs.cancer_type y from genie_mutation gm, genie_sample gs  ")
		.append(" where gm.hugo_symbol = :geneTerm and gm.amino_acid_notation = :aminoAcidNotation ")
		.append(" and gm.genie_sample_id = gs.genie_sample_id ")
		.append(" group by gs.cancer_type ")
		.append(" order by x desc limit 9; ");
		
        query = session.createNativeQuery(sb.toString())
		.setParameter("geneTerm", geneTerm)
		.setParameter("aminoAcidNotation", aminoAcidNotation);

        List<String> yDataCancer = query.setResultTransformer(new ResultTransformer() {
		private static final long serialVersionUID = 1L;
	
		@Override
		public Object transformTuple(Object[] values, String[] labels) {
			return new GenericBarPlotData(values, labels);
		}
	
		@SuppressWarnings("rawtypes")
		@Override
		public List transformList(List arg0) {
			return arg0;
		}
	}).list().stream().map(c -> c.getY()).collect(Collectors.toList());
		
		sb = new StringBuilder();
		sb.append(" select count(gm.variant_change) x, gm.variant_change aa, gs.cancer_type cancerType from genie_mutation gm, genie_sample gs ") 
		.append(" where gm.hugo_symbol = :geneTerm and gm.amino_acid_notation = :aminoAcidNotation ")
		.append(" and gm.genie_sample_id = gs.genie_sample_id ")
		.append(" group by gm.variant_change, gs.cancer_type ")
		.append(" order by x desc; ");
		Query<CondonDistribution> query2 = session.createNativeQuery(sb.toString())
				.setParameter("geneTerm", geneTerm)
				.setParameter("aminoAcidNotation", aminoAcidNotation);
		
		List<CondonDistribution> codonData = query2.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new CondonDistribution(values, labels);
			}
			
		}).list();
		Map<String, Map<String, CondonDistribution>> codonDataByCancerType = new HashMap<String, Map<String, CondonDistribution>>();
		for (CondonDistribution cd : codonData) {
			if (!yDataCancer.contains(cd.getCancerType())) {
				cd.setCancerType("Other");
			}
			Map<String, CondonDistribution> byAA = codonDataByCancerType.get(cd.getCancerType());
			if (byAA == null) {
				byAA = new HashMap<String, CondonDistribution>();
			}
			byAA.put(cd.getAa(), cd);
			codonDataByCancerType.put(cd.getCancerType(), byAA);
		}
		yDataCancer.add("Other");
		List<Trace> traces = new ArrayList<Trace>();
		Map<String, Integer> countsPerAA= new HashMap<String, Integer>();
//		Set<String> traceNames = codonData.stream().map(c -> c.getCancerType()).collect(Collectors.toSet());
		for (String name : yDataCancer) {
			for (GenericBarPlotData aa : yData) {
				Map<String, CondonDistribution> byAA = codonDataByCancerType.get(name);
				if (byAA != null) {
					CondonDistribution cd = byAA.get(aa.getY());
					if (cd != null) {
						Integer count = countsPerAA.get(aa.getY());
						if (count == null) {
							count = 0;
						}
						count += cd.getCount().intValue();
						countsPerAA.put(aa.getY(), count);
					}
				}
			}
		}
		for (String name : yDataCancer) {
			Trace trace = new Trace();
			for (GenericBarPlotData aa : yData) {
				Map<String, CondonDistribution> byAA = codonDataByCancerType.get(name);
				if (byAA != null) {
					CondonDistribution cd = byAA.get(aa.getY());
					if (cd != null) {
						trace.addX(cd.getCount());
					}
					else {
						trace.addX(null);
					}
				}
				else {
					
				}
				trace.addY(aa.getY() + " ");
				
				AminoAcid aaPOJO = LookupUtils.PROTEIN_1_TO_3_MAP.get(aa.getY());
				if (aaPOJO == null) { //try with '='
					aaPOJO = LookupUtils.PROTEIN_1_TO_3_MAP.get(aminoAcidNotation.substring(0, 1));
				}
				String aaName = "";
				if (aaPOJO != null) {
					aaName = StringUtils.capitalize(aaPOJO.getFullName());
				}
				trace.addLabel(aaName + ": " + countsPerAA.get(aa.getY()));
				
				
			}
			if (trace.getY() != null && !trace.getY().isEmpty()) {
				trace.setHovertemplate("%{text}<extra></extra>");
				trace.setName(name);
				traces.add(trace);
			}
		}
		return traces;
	}
	
	@Transactional
	public List<GenieMutation> getGenieMutationFromPostion(String chr, String hugoSymbol, Integer position) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from genie_mutation where chr = :chr ")
		.append(" and hugo_symbol = :hugoSymbol and start_pos = :position");
		return session.createNativeQuery(sb.toString(), GenieMutation.class)
		.setParameter("hugoSymbol", hugoSymbol)
		.setParameter("chr", chr)
		.setParameter("position", position)
		.list();
	}
	
	@Transactional
	public List<MSKHotspot> getMSKHotspots(String hugoSymbol, String residue) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from MSKHotspot where hugoSymbol = :hugoSymbol and residue = :residue";
		List<MSKHotspot> hotspots = session.createQuery(hql, MSKHotspot.class)
				.setParameter("hugoSymbol", hugoSymbol)
				.setParameter("residue", residue)
				.list();
		return hotspots;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<GenericBarPlotData> getGeniePatientCountForCNV(String hugoSymbol, String ampDel) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select count(oncotree_code) as x,oncotree_code as y")
		.append(" from ( ")
		.append(" select gs.oncotree_code from genie_cna gc, genie_sample gs ")
		.append(" where gc.genie_sample_id = gs.genie_sample_id ")
		.append(" and gc.hugo_symbol = :hugoSymbol ");
		if (ampDel.equals("AMPLIFICATION")) {
			sb.append(" and cna_value > 1 ");
		}
		else {
			sb.append(" and cna_value < -1 ");
		}
		sb.append(" group by gs.patient_id, gs.oncotree_code) as sub ")
		.append(" group by oncotree_code ")
		.append(" order by x desc ")
		.append(" limit 10 ");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("hugoSymbol", hugoSymbol);
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<RatioBarPlotData> getGeniePatientPctForCNV(String hugoSymbol, String ampDel) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		
		sb.append("select x, y, gcc.case_count as total, x / gcc.case_count as ratio from  ")
		.append(" (select count(oncotree_code) as x,oncotree_code as y ")
		.append(" from (  ")
		.append(" select gs.oncotree_code from genie_cna gc, genie_sample gs  ")
		.append(" where gc.genie_sample_id = gs.genie_sample_id   ")
		.append("  and gc.hugo_symbol = :hugoSymbol ");
		if (ampDel.equals("AMPLIFICATION")) {
			sb.append(" and cna_value > 1 ");
		}
		else {
			sb.append(" and cna_value < -1 ");
		}
		sb.append(" group by gs.patient_id, gs.oncotree_code) as sub  ")
		.append(" group by oncotree_code) as sub2, genie_cna_count gcc ")
		.append(" where sub2.y = gcc.oncotree_code ")
		.append(" and gcc.hugo_symbol = :hugoSymbol ")
		.append(" and x >= 5 ")
		.append(" order by ratio desc ")
		.append(" limit 10; ");
		
		Query<RatioBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("hugoSymbol", hugoSymbol);
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new RatioBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();

	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<GenericBarPlotData> getGeniePatientCountForFusion(String geneFive, String geneThree) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		String fiveThree = geneFive + "-" + geneThree;
		String threeFive = geneThree + "-" + geneFive;
		
		sb.append("select count(oncotree_code) as x,oncotree_code as y ")
		.append(" from (  ")
		.append(" select gs.oncotree_code from genie_fusion gf, genie_sample gs  ")
		.append(" where gf.genie_sample_id = gs.genie_sample_id  ")
		.append(" and ( gf.fusion_name like :fiveThree or gf.fusion_name like :threeFive )")
		.append(" group by gs.patient_id, gs.oncotree_code) as sub  ")
		.append(" group by oncotree_code  ")
		.append(" order by x desc  ")
		.append(" limit 10;  ");
		
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("fiveThree", "%" + fiveThree + " %")
				.setParameter("threeFive", "%" + threeFive + " %");
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<RatioBarPlotData> getGeniePatientPctForFusion(String geneFive, String geneThree) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		String fiveThree = geneFive + "-" + geneThree;
		String threeFive = geneThree + "-" + geneFive;
		sb.append("select x, y, gcc.case_count as total, x / gcc.case_count as ratio from  ")
		.append(" ( ")
		.append(" select count(oncotree_code) as x,oncotree_code as y ")
		.append(" from (  ")
		.append(" select gs.oncotree_code from genie_fusion gf, genie_sample gs  ")
		.append(" where gf.genie_sample_id = gs.genie_sample_id  ")
		.append(" and ( gf.fusion_name like :fiveThree or gf.fusion_name like :threeFive )")
		.append(" group by gs.patient_id, gs.oncotree_code) as sub  ")
		.append(" group by oncotree_code ) as sub2, genie_fusion_count gcc ")
		.append(" where sub2.y = gcc.oncotree_code ")
		.append(" and x >= 5 ")
		.append(" order by ratio desc ")
		.append(" limit 10; ");
		
		Query<RatioBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("fiveThree", "%" + fiveThree + " %")
				.setParameter("threeFive", "%" + threeFive + " %");
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new RatioBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<Object> populateGenieFusionCountTable() {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		sb.append("select count(gs.oncotree_code) x, gs.oncotree_code y from genie_fusion gf, genie_sample gs ")
		.append(" where gf.genie_sample_id = gs.genie_sample_id ")
		.append(" group by gs.oncotree_code;");
		
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString());
		
		List<Object> result = query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list().stream().map(g -> new GenieFusionCount(
				g.getY(), 
				g.getX()
				.intValue()))
				.collect(Collectors.toList());
		return result;
	}
	
	@Transactional
	public Map<String, CosmicFusion> getAllCosmicFusionsAsMap() {
		Session session = sessionFactory.getCurrentSession();
		Map<String, CosmicFusion> fusionsByFusionId = new HashMap<String, CosmicFusion>();
		String hql = "from CosmicFusion";
		List<CosmicFusion> fusions = session.createQuery(hql, CosmicFusion.class).list();
		for (CosmicFusion c : fusions) {
			fusionsByFusionId.put(c.getFusionId(), c);
		}
		return fusionsByFusionId;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Transactional
	public List<GenericBarPlotData> getCosmicExonBreakpointForGene(String fiveGene, String threeGene, boolean isFiveGene) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sb = new StringBuilder();
		String fiveOrThree = "three";
		if (isFiveGene) {
			fiveOrThree = "five";
		}
		sb.append(" select * from ( ")
				.append(" select cf.").append(fiveOrThree).append("_exon y, count(cf.five_exon) x ")
				.append(" from cosmic_sample_fusion csf, cosmic_fusion cf ")
				.append(" where csf.cosmic_fusion_id = cf.cosmic_fusion_id ")
				.append(" and five_gene = :fiveGene and three_gene = :threeGene ")
				.append(" group by cf.").append(fiveOrThree).append("_exon) as sub ")
				.append(" order by x desc; ");
		Query<GenericBarPlotData> query = session.createNativeQuery(sb.toString())
				.setParameter("fiveGene", fiveGene)
				.setParameter("threeGene", threeGene);
		
		return query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] values, String[] labels) {
				return new GenericBarPlotData(values, labels);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public List transformList(List arg0) {
				return arg0;
			}
		}).list();
	}

	@Transactional
	public ClinicalTest getClinicalTest(String labTestName) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "from ClinicalTest where test_name = :labTestName";
		return session.createQuery(hql, ClinicalTest.class)
		.setParameter("labTestName", labTestName)
		.uniqueResult();
	}
}
