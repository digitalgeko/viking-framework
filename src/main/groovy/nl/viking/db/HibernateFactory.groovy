package nl.viking.db

import com.liferay.portal.kernel.util.PropsUtil
import groovy.transform.Synchronized
import nl.viking.Conf
import nl.viking.VikingPortlet
import nl.viking.db.hibernate.strategy.VikingNamingStrategy
import nl.viking.logging.Logger
import nl.viking.utils.ReflectionUtils
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment

import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 6/20/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
class HibernateFactory {

	private static SessionFactory sessionFactory;

	private static EntityManagerFactory entityManagerFactory

	private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal();

	static final Configuration cfg = new Configuration();

	synchronized static EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			// Defaults
			cfg.setProperty("hibernate.connection.driver_class", PropsUtil.get("jdbc.default.driverClassName"))
			cfg.setProperty("hibernate.connection.url", PropsUtil.get("jdbc.default.url"))
			cfg.setProperty("hibernate.connection.username", PropsUtil.get("jdbc.default.username"))
			cfg.setProperty("hibernate.connection.password", PropsUtil.get("jdbc.default.password"))

//			cfg.setProperty("hibernate.current_session_context_class", "thread")
			cfg.setProperty(Environment.CONNECTION_PROVIDER, "com.zaxxer.hikari.hibernate.HikariConnectionProvider")

			if (Conf.properties.hibernate.prefix) {
				cfg.setNamingStrategy(new VikingNamingStrategy())
			}

			Conf.properties.hibernate.flatten().each {
				cfg.setProperty("hibernate."+it.key, it.value)
			}

			// Hibernate entities
			def additionalModelPackages = Conf.properties.models.additionalPackages ?: []
			def modelPackages = ['models'] + additionalModelPackages

			def modelClasses = ReflectionUtils.getModelClassesWithAnnotations(modelPackages, Entity.class)
			modelClasses.each { type ->
				cfg.addAnnotatedClass(type);
			}

			entityManagerFactory = Persistence.createEntityManagerFactory( "viking", cfg.getProperties() );
		}

		return entityManagerFactory
	}

	synchronized static SessionFactory  getSessionFactory() {
		if (sessionFactory == null) {
			// Defaults
			cfg.setProperty("hibernate.connection.driver_class", PropsUtil.get("jdbc.default.driverClassName"))
			cfg.setProperty("hibernate.connection.url", PropsUtil.get("jdbc.default.url"))
			cfg.setProperty("hibernate.connection.username", PropsUtil.get("jdbc.default.username"))
			cfg.setProperty("hibernate.connection.password", PropsUtil.get("jdbc.default.password"))

//			cfg.setProperty("hibernate.current_session_context_class", "thread")
			cfg.setProperty(Environment.CONNECTION_PROVIDER, "com.zaxxer.hikari.hibernate.HikariConnectionProvider")

			if (Conf.properties.hibernate.prefix) {
				cfg.setNamingStrategy(new VikingNamingStrategy())
			}

			Conf.properties.hibernate.flatten().each {
				cfg.setProperty("hibernate."+it.key, it.value)
			}

			// Hibernate entities
			def additionalModelPackages = Conf.properties.models.additionalPackages ?: []
			def modelPackages = ['models'] + additionalModelPackages

			def modelClasses = ReflectionUtils.getModelClassesWithAnnotations(modelPackages, Entity.class)
			modelClasses.each { type ->
				cfg.addAnnotatedClass(type);
			}

			def serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();

			sessionFactory = cfg.buildSessionFactory(serviceRegistry);
		}

		return sessionFactory
	}

	@Synchronized
	synchronized static EntityManager getCurrentEntityManager () {
		def em = entityManagerThreadLocal.get()
		if (!em) {
			em = getEntityManagerFactory().createEntityManager()
			entityManagerThreadLocal.set(em)
		}
		return em
	}

	@Synchronized
	synchronized static closeCurrentEntityManager() {
		def em = getCurrentEntityManager()
		if (em) {
			em.close()
			entityManagerThreadLocal.remove()
		}
	}

	@Synchronized
	synchronized static withEntityManager (Closure closure) {
		EntityManager entityManager = getCurrentEntityManager()

		def returnValue
		try {
			entityManager.transaction.begin()
			returnValue = closure(entityManager)
			entityManager.transaction.commit()
		} catch (Exception e) {
			Logger.error(e, "*********** Hibernate problem ***********")
			entityManager.transaction.rollback();
		} finally {
			if (!VikingPortlet.currentController) {
				closeCurrentEntityManager()
			}
		}
		returnValue


	}


	static void destroy() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close()
			entityManagerFactory = null
		}
		if (sessionFactory != null) {
			closeCurrentEntityManager()
			sessionFactory.close()
			sessionFactory = null
		}
	}
}
