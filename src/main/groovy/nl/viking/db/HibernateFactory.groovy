package nl.viking.db

import com.liferay.portal.kernel.util.PropsUtil
import com.liferay.portal.security.permission.PermissionThreadLocal
import com.liferay.portal.util.PortalUtil
import groovy.transform.Synchronized
import nl.viking.Conf
import nl.viking.VikingPortlet
import nl.viking.db.hibernate.strategy.VikingNamingStrategy
import nl.viking.logging.Logger
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import org.reflections.Reflections

import javax.persistence.Entity

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 6/20/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
class HibernateFactory {

	private static SessionFactory sessionFactory;

	public static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal();

	static final Configuration cfg = new Configuration();

	synchronized static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			// Defaults
			cfg.setProperty("hibernate.connection.driver_class", PropsUtil.get("jdbc.default.driverClassName"))
			cfg.setProperty("hibernate.connection.url", PropsUtil.get("jdbc.default.url"))
			cfg.setProperty("hibernate.connection.username", PropsUtil.get("jdbc.default.username"))
			cfg.setProperty("hibernate.connection.password", PropsUtil.get("jdbc.default.password"))

			cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "org.hibernate.context.ThreadLocalSessionContext")
			cfg.setProperty(Environment.CONNECTION_PROVIDER, "com.jolbox.bonecp.provider.BoneCPConnectionProvider")

			if (Conf.properties.hibernate.prefix) {
				cfg.setNamingStrategy(new VikingNamingStrategy())
			}

			Conf.properties.bonecp.flatten().each {
				cfg.setProperty("bonecp."+it.key, it.value.toString())
			}

			Conf.properties.hibernate.flatten().each {
				cfg.setProperty("hibernate."+it.key, it.value)
			}

			// Hibernate entities
			def additionalModelPackages = Conf.properties.models.additionalPackages ?: []
			def modelPackages = ['models'] + additionalModelPackages

			modelPackages.each {
				def reflections = new Reflections(it)
				Set<Class<?>> modelClasses = reflections.getTypesAnnotatedWith(Entity.class) + reflections.getTypesAnnotatedWith(org.hibernate.annotations.Entity.class);
				modelClasses.each{ type ->
					cfg.addAnnotatedClass(type);
				}
			}
//			cfg.addAnnotatedClass(VikingMessageEntry.class);
			sessionFactory = cfg.buildSessionFactory();
		}

		return sessionFactory
	}

	static Session getCurrentSession () {
		Session session = sessionThreadLocal.get()
		if (session) {
			return session;
		} else {
			session = HibernateFactory.sessionFactory.openSession()
			sessionThreadLocal.set(session)
			return session
		}
	}

	static closeCurrentSession() {
		Session session = sessionThreadLocal.get()
		if (session) {
			session.close()
			sessionThreadLocal.remove()
		}
	}

	@Synchronized
	synchronized static withSession (Closure closure) {
		Session session = getCurrentSession();
		def returnValue = null
		try{
			session.beginTransaction();
			returnValue = closure(session)
			session.transaction.commit();
		} catch (Exception e) {
			session.transaction.rollback();
			Logger.error(e, "Hibernate problem")
		} finally {
			if (!VikingPortlet.currentController) {
				closeCurrentSession()
			}
		}

		returnValue
	}


	static void destroy() {
		if (sessionFactory != null) {
			sessionFactory.close()
			sessionFactory = null
		}
	}
}
