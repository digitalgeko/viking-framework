package nl.viking.db

import com.liferay.portal.kernel.util.PropsUtil
import nl.viking.Conf

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/2/13
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
class GMongoProps {

    public static String getDBHost() {
		if (PropsUtil.contains("mongo.db.host")) {
			return PropsUtil.get("mongo.db.host")
		}
        return Conf.properties.mongo.db.host;
    }

	public static String[] getDBServerAddresses() {
		if (PropsUtil.contains("mongo.db.serverAddresses")) {
			return PropsUtil.getArray("mongo.db.serverAddresses")
		}
		return Conf.properties.mongo.db.serverAddresses;
	}

	public static String getDBServerAddressesString() {
		if (PropsUtil.contains("mongo.db.serverAddresses")) {
			return PropsUtil.get("mongo.db.serverAddresses")
		}
		return Conf.properties.mongo.db.serverAddresses;
	}

    public static Integer getDBPort() {
		if (PropsUtil.contains("mongo.db.port")) {
			return new Integer(PropsUtil.get("mongo.db.port"))
		}
        return Conf.properties.mongo.db.port?:27017;
    }

    public static String getDBName() {
		if (PropsUtil.contains("mongo.db.name")) {
			return PropsUtil.get("mongo.db.name")
		}
        return Conf.properties.mongo.db.name;
    }

    public static String getDBUsername() {
		if (PropsUtil.contains("mongo.db.username")) {
			return PropsUtil.get("mongo.db.username")?:null
		}
        return Conf.properties.mongo.db.username?:null;
    }

    public static String getDBPassword() {
		if (PropsUtil.contains("mongo.db.password")) {
			return PropsUtil.get("mongo.db.password")?:null
		}
        return Conf.properties.mongo.db.password?:null;
    }

}

