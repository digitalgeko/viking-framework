package nl.viking.logging

import nl.viking.Conf

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/20/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
class Logger {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Conf.FRAMEWORK_NAME);

	static void trace(Object message) {
		logger.trace(message)
	}
	static void debug(Object message) {
		logger.debug(message)
	}
	static void info(Object message) {
		logger.info(message)
	}
	static void warn(Object message) {
		logger.warn(message)
	}
	static void error(Object message) {
		logger.error(message)
	}
	static void fatal(Object message) {
		logger.fatal(message)
	}

    static void trace(String message, Object... args) {
        logger.trace(format(message, args))
    }
    static void debug(String message, Object... args) {
        logger.debug(format(message, args))
    }
    static void debug(Throwable e, String message, Object... args) {
        logger.debug(format(message, args), e)
    }
    static void info(String message, Object... args) {
        logger.info(format(message, args))
    }
    static void info(Throwable e, String message, Object... args) {
        logger.info(format(message, args), e)
    }
    static void warn(String message, Object... args) {
        logger.warn(format(message, args))
    }
    static void warn(Throwable e, String message, Object... args) {
        logger.warn(format(message, args), e)
    }
    static void error(String message, Object... args) {
        logger.error(format(message, args))
    }
    static void error(Throwable e, String message, Object... args) {
        logger.error(format(message, args), e)
    }
    static void fatal(String message, Object... args) {
        logger.fatal(format(message, args))
    }
    static void fatal(Throwable e, String message, Object... args) {
        logger.fatal(format(message, args), e)
    }

    static String format(String message, Object... args){
        String.format(message, args)
    }
}
