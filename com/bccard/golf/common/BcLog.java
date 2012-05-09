/*******************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*  Ŭ������		:   BcLog.java
*  �ۼ���			:   e4net
*  ����				:   �αױ��
*  �������		:   etax
*  �ۼ�����		:   2007.01.04
************************** �����̷� ********************************************
* ����            ������         ������� 
*
*******************************************************************************/

package com.bccard.golf.common;

import org.apache.log4j.*;

/**
* �αױ��
* @version 2007.01.04
* @author e4net
*/ 
public class BcLog {
    public static final String ACCESS_LOG = "BcAccessLog";
    public static final String MEMBER_LOG = "BcMemberLog";
    public static final String JOLT_LOG   = "BcJoltLog";
    public static final String JOLT_LOG2   = "BcJoltLog2";
    public static final String ADMIN_LOG  = "BcAdminLog";
    public static final String APP_LOG  = "BcAppLog";
	public static final String SETTLE_LOG  = "BcSettleLog";

    private static Logger accessLogger;
    private static Logger memberLogger;
    private static Logger joltLogger;
    private static Logger joltLogger2;
    private static Logger adminLogger;
    private static Logger appLogger;
	private static Logger settleLogger;

	/**
    * configureAndWatch
    * @param N/A
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void configureAndWatch() {
        //PropertyConfigurator.configureAndWatch("log4j");
    }

	/**
    * accessLog
    * @param message String Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void accessLog(String message) {
        BcLog.accessLog(message,null);
    }

	/**
    * accessLog
    * @param message String Object
    * @param t Throwable Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void accessLog(String message, Throwable t) {
        try {
            if ( accessLogger == null ) {
                accessLogger = Logger.getLogger(ACCESS_LOG);
            }
            if ( t != null )  {
                accessLogger.info(message,t);
            } else {
                accessLogger.info(message);
            }
        } catch(Throwable ignore) {
            
        }
    }

	/**
    * memberLog
    * @param message String Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void memberLog(String message) {
        BcLog.memberLog(message,null);
    }

	/**
    * memberLog
    * @param message String Object
    * @param t Throwable Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void memberLog(String message, Throwable t) {
        try {
            if ( memberLogger == null ) {
                memberLogger = Logger.getLogger(MEMBER_LOG);
            }
            if ( t != null )  {
                memberLogger.info(message,t);
            } else {
                memberLogger.info(message);
            }
        } catch(Throwable ignore) {
            
        }
    }

	/**
    * joltLog
    * @param message String Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void joltLog(String message) {
        BcLog.joltLog(message,null);
    }

	/**
    * joltLog
    * @param message String Object
    * @param t Throwable Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void joltLog(String message, Throwable t) {
        try {
            if ( joltLogger == null ) {
                joltLogger = Logger.getLogger(JOLT_LOG);
            }
            if ( t != null )  {
                joltLogger.info(message,t);
            } else {
                joltLogger.info(message);
            }
        } catch(Throwable ignore) {
            
        }
    }
    
    /**
     * joltLog
     * @param message String Object
     * @version 2007.01.04
  	* @author e4net
     * @return N/A
     */
     public static void joltLog2(String message) {
         BcLog.joltLog2(message,null);
     }

 	/**
     * joltLog2
     * @param message String Object
     * @param t Throwable Object
     * @version 2007.01.04
  	* @author e4net
     * @return N/A
     */
     public static void joltLog2(String message, Throwable t) {
         try {
             if ( joltLogger2 == null ) {
                 joltLogger2 = Logger.getLogger(JOLT_LOG2);
             }
             if ( t != null )  {
                 joltLogger2.info(message,t);
             } else {
                 joltLogger2.info(message);
             }
         } catch(Throwable ignore) {
             
         }
     }
    

	/**
    * adminLog
    * @param message String Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void adminLog(String message) {
        BcLog.adminLog(message,null);
    }

	/**
    * adminLog
    * @param message String Object
    * @param t Throwable Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void adminLog(String message, Throwable t) {
        try {
            if ( adminLogger == null ) {
                adminLogger = Logger.getLogger(ADMIN_LOG);
            }
            if ( t != null )  {
                adminLogger.info(message,t);
            } else {
                adminLogger.info(message);
            }
        } catch(Throwable ignore) {
            
        }
    }

	/**
    * appLog
    * @param message String Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void appLog(String message) {
        BcLog.appLog(message,null);
    }

	/**
    * appLog
    * @param message String Object
    * @param t Throwable Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void appLog(String message, Throwable t) {
        try {
            if ( appLogger == null ) {
                appLogger = Logger.getLogger(APP_LOG);
            }
            if ( t != null )  {
                appLogger.info(message,t);
            } else {
                appLogger.info(message);
            }
        } catch(Throwable ignore) {
            
        }
    }

	/**
    * settleLog
    * @param message String Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void settleLog(String message) {
        BcLog.settleLog(message,null);
    }

	/**
    * appLog
    * @param message String Object
    * @param t Throwable Object
    * @version 2007.01.04
 	* @author e4net
    * @return N/A
    */
    public static void settleLog(String message, Throwable t) {
        try {
            if ( settleLogger == null ) {
                settleLogger = Logger.getLogger(SETTLE_LOG);
            }
            if ( t != null )  {
                settleLogger.info(message,t);
            } else {
                settleLogger.info(message);
            }
        } catch(Throwable ignore) {
            
        }
    }

}