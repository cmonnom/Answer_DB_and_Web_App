package utsw.bicf.answer.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utsw.bicf.answer.model.Annotation;
import utsw.bicf.answer.model.Gene;
import utsw.bicf.answer.model.OrderCase;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantSelected;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<Annotation> getAnnotationsForOrganisationAndUser(String origin, User user, Gene gene) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sql = new StringBuilder("select * from annotation where origin = :origin ")
				.append(" and gene_id = :geneId ").append(" and !deleted ");
		if (user != null) {
			sql.append(" and answer_user_id = :user ");
		}
		Query query = session.createNativeQuery(sql.toString(), Annotation.class)
				.setParameter("geneId", gene.getGeneId()).setParameter("origin", origin);
		if (user != null) {
			query.setParameter("user", user);
		}
		return query.list();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public Gene getGeneBySymbol(String symbol) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder sql = new StringBuilder("select * from gene where symbol = :symbol ");
		Query query = session.createNativeQuery(sql.toString(), Gene.class).setParameter("symbol", symbol);
		List<Gene> genes = query.list();
		if (genes != null && genes.size() == 1) {
			return genes.get(0);
		}
		return null;
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

	@SuppressWarnings("unchecked")
	@Transactional
	public VariantSelected getVariantSelectedByGeneVariantAndCase(String geneAndVariant, OrderCase orderCase) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder(
				"from VariantSelected where geneAndVariant = :geneAndVariant and orderCase = :orderCase ");
		Query query = session.createQuery(hql.toString(), VariantSelected.class)
				.setParameter("geneAndVariant", geneAndVariant)
				.setParameter("orderCase", orderCase);
		List<VariantSelected> variants = query.list();
		if (variants != null && variants.size() == 1) {
			return variants.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<VariantSelected> getAllVariantsSelectedByCase(OrderCase orderCase) {
		Session session = sessionFactory.getCurrentSession();
		StringBuilder hql = new StringBuilder(
				"from VariantSelected where orderCase = :orderCase ");
		Query query = session.createQuery(hql.toString(), VariantSelected.class)
				.setParameter("orderCase", orderCase);
		return query.list();
	}

}
