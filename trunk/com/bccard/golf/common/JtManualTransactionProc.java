/***************************************************************************************************
*   클래스명  : JtManualTransactionProc
*   작성자    :
*   내용      :
*   적용범위  : bccard.com
*   작성일자  : 2005.7.21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.initech.JtControl;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;



/** ****************************************************************************
 * Proc Class for Transactional BCCARD TUXEDO SERVICE.
 **************************************************************************** */
public class JtManualTransactionProc extends AbstractObject {
	protected static final String SYS_ERROR = "JT99999";
	protected static final String POOL_NAME = "golfnew_XA";
	protected final static String ERROR_LOG = "ERR|";
	protected final static String UNKNOWN_ERROR = "ERR|UNKNOWN";

	/**
     * execute 
    */
	public JtControl execute(WaContext context, HttpServletRequest request  , JoltInput input) throws TaoException {
		Properties properties = new Properties();
		properties.setProperty("POOL_NAME", POOL_NAME );
		return this.execute(context, request, input, properties);
	}
 

	/** ****************************************************************************
	 * Transactional Jolt
	 **************************************************************************** */
	public JtControl execute(WaContext context
                            , HttpServletRequest request
                            , JoltInput input
                            , Properties properties) throws TaoException {
		
		this.wickedPropertyCheck(properties);
		TaoConnection con  = null;
		JoltOutput output = null;

		

	    try {
			
			/* ******************************************************************
			 * Get connection, fetch encapsulated  ServletResult
			 *******************************************************************/
	    	debug("JtManualTransactionProc >> input >> " + input.toString());


			con = context.getTaoConnection("jolt", properties);

			con.begin(60);
			//con.begin(input.getTransactionTimeOut());

			output = (JoltOutput)con.execute(input);
debug("JtManualTransactionProc >> output >> "+output);


			return new JtTransactionControl(con, input, output);
		} catch ( TaoException t ) {
			BcLog.joltLog(ERROR_LOG + t.getKey());
            BcLog.joltLog(t.getMessage(),t);
            throw t;
        } catch ( Throwable t ) {
			BcLog.joltLog(UNKNOWN_ERROR);
            BcLog.joltLog(t.getMessage(),t);
            throw new TaoException(t);
        }
    }

    /**
     * wickedPropertyCheck
    */
	private void wickedPropertyCheck(Properties prop) {
		try {
			prop.setProperty("golfnew_XA", "tr");
			prop.setProperty("golfnew_non", "nt");
			prop.setProperty("DEFAULT_FACTORY", "joltProto");
			prop.setProperty("LOGGER", "off");
			prop.setProperty("POOL_MANAGER_LOAD_TYPE", "poolmgr");

			if (!prop.containsKey("POOL_NAME")) { prop.setProperty("POOL_NAME", "golfnew_XA"); }
		} catch (Exception ignore) {}
	}


	/* * ****************************************************************************
	 * Transaction result parsing method to determine if COMMIT or ROLLBACK
	 **************************************************************************** */
	//	public abstract boolean decision(JoltInput input, JoltOutput output) throws TaoException;

	/* * ****************************************************************************
	 * Transaction result information method
	 **************************************************************************** */
	//	public abstract String getErrorCode(JoltInput input, JoltOutput output);
    /**
     * getJtOutput
    */
	private class JtTransactionControl implements JtControl {
		TaoConnection con;
		private JoltOutput jout;
		private boolean committer = true;
		// for debugging. later delete.
		private JoltInput jin;

		/**
		 * JtTransactionControl
		*/
		JtTransactionControl(TaoConnection con, JoltOutput jout) {
			this.con = con;
			this.jout = jout;
		}

		// for debugging. later delete
		/**
		 * JtTransactionControl
		*/
		JtTransactionControl(TaoConnection con, JoltInput jin, JoltOutput jout) {
			this.con = con;
			this.jout = jout;
			this.jin = jin;
			debug("JMT_DBUG|"+ jin.getServiceName());
		}

		/**
		 * getJtOutput
		*/
		public JoltOutput getJtOutput() { return this.jout; }

		/**
		 * commit
		*/
		public void commit() throws BaseException {
			try {
				this.con.commit();	committer = false;
				debug("JMT_DBUG|"+ jin.getServiceName() + "|COMMITTED");
				//this.jout.setTransactionResult(true);
			} catch (TaoException e) {
				throw new BaseException();
			}
		}

		/**
		 * rollback
		*/
		public void rollback() throws BaseException {
			try {
				this.con.rollback();	committer = false;
				debug("JMT_DBUG|"+ jin.getServiceName() + "|ROLLEDBACK");
				//this.jout.setTransactionResult(false);
			} catch (TaoException e) {
				throw new BaseException();
			}
		}

		/**
		 * close
		*/
		public void close() throws TaoException {
			try {
				if (this.committer) {
					this.con.rollback();
					//this.jout.setTransactionResult(false);
				}
			} catch (TaoException e) {
				throw e;
			}
		}

	}
}
