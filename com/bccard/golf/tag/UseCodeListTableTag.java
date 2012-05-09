/******************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� : 2004. 12. 24 altair
 * ���� : ��������  �ǿ���
 * ���� :  
 * ���� : 
 ******************************************************************************/
package com.bccard.golf.tag;

import java.sql.*;
import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.apache.log4j.Logger;

import com.bccard.waf.action.JspContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.CodeImpl;
import com.bccard.waf.dao.DbConnectionFactory;
import com.bccard.waf.logging.WaLogger;
import com.bccard.waf.tag.UseLoopSupport;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class UseCodeListTableTag extends UseLoopSupport {
	private transient Logger logger;

	private String key;
	private String codeFld;
	private String nameFld;
	private String whereFld = "";
	private String orderFld = "";
	private String resourceScope;

	protected Iterator iterator;

	/**
	 * @info UseCodeListTableTag
	 * @return 
	 */
	public UseCodeListTableTag() {}

	/**
	 * @info initLogger
	 * @return void 
	 */
	private void initLogger() {
		logger = WaLogger.getLogger(getClass().getName());
	}

	/**
	 * @info debug
	 * @param String s
	 * @return void 
	 */
	protected void debug(String s) {
		if (logger == null)
			initLogger();
		logger.debug(s);
	}

	/**
	 * @info debug
	 * @param String s
	 * @param Throwable throwable
	 * @return void 
	 */
	protected void debug(String s, Throwable throwable) {
		if (logger == null)
			initLogger();
		logger.debug(s, throwable);
	}

	/**
	 * @info next
	 * @return Object 
	 */
	protected Object next() throws JspTagException {
		if (iterator == null)
			return null;
		else
			return iterator.next();
	}

	/**
	 * @info hasNext
	 * @return boolean 
	 */
	protected boolean hasNext() throws JspTagException {
		if (iterator == null)
			return false;
		else
			return iterator.hasNext();
	}

	/**
	 * resourceScope�� ���� �ڵ尴ü �غ��ϱ�
	 */
	protected void prepare() throws JspTagException {
		Object obj = null;
		JspContext jspcontext = new JspContext(pageContext);
		// todo - ���ҽ� ���·� ��������� ���� �ø��� ����..??
		/*
		 if (resourceScope == null)
		 obj = jspcontext.getCodeList(key);
		 else
		 obj = jspcontext.getCodeList(key, resourceScope);
		 */
		obj = getCustomCode();
		if (obj == null)
			obj = new ArrayList();
		iterator = ((List) (obj)).iterator();

	}

	/**
	 * table, codeFld, nameFld�� ����Ͽ� �ڵ尴ü �����
	 * @return
	 */
	private List getCustomCode() {

		String sql = "";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultset = null;

		//CodeMap codemap = new CodeMap(key);
		ArrayList cm = new ArrayList();
		String sOrder = "1";
		if (!orderFld.trim().equals("")) {
			sOrder = orderFld;
		}
		//super.resourceMap = new HashMap();
		sql = "select " + codeFld + " cd, " + nameFld + " nm, " + 
			codeFld + "||';'||" + nameFld + " alias " + 
				" from bcdba." + key + 
				" where 1=1 " + whereFld +
				" order by " + sOrder;
		try {
			DbConnectionFactory dbconnectionfactory = DbConnectionFactory
					.createFactory("com.bccard.town.common.CpnDbConnectionFactory");
			connection = dbconnectionfactory.getConnection(null);
			statement = connection.createStatement();
			for (resultset = statement.executeQuery(sql); resultset.next();) {
				String s = null;
				CodeImpl codeimpl = new CodeImpl();
				try {
					codeimpl.setKey(key);
					codeimpl.setCode(resultset.getString("cd"));

					String s2 = StrUtil.isNull(resultset.getString("nm"), "");
					codeimpl.setMiddle(s2);

					String s3 = StrUtil.isNull(resultset.getString("alias"), "");
					codeimpl.setAlias(s3);

					//codemap.addCode(codeimpl);
					cm.add(codeimpl);
				} catch (Throwable throwable1) {
					if (s != null && codeimpl != null) {
						debug("CodeResource \"" + sql + "\" load failed.");
						debug("\n\t" + s + "(" + codeimpl.getCode() + ")", throwable1);
					}
				}
			}

		} catch (Throwable throwable) {
			debug("UseCodeListTableTag \"" + sql + "\" load failed.", throwable);
		} finally {
			try {
				if (resultset != null)
					resultset.close();
			} catch (Throwable throwable2) {}
			try {
				if (statement != null)
					statement.close();
			} catch (Throwable throwable3) {}
			try {
				if (connection != null)
					connection.close();
			} catch (Throwable throwable4) {}
		}

		//return codemap.getCodeList();	// bcwaf���� ���ڴ�� �����ϱ⶧���� ���Ұ�..!!
		return cm;
	}

	/**
	 * @info release
	 * @return void 
	 */
	public void release() {
		super.release();
		key = null;
		resourceScope = null;
	}

	/**
	 * @info setResourceScope
	 * @param String s
	 * @return void 
	 */
	public void setResourceScope(String s) {
		if (s != null && ("context".equals(s) || "servlet".equals(s)))
			resourceScope = s;
	}

	/**
	 * @info setBegin
	 * @param int i
	 * @return void 
	 */
	public void setBegin(int i) throws JspTagException {
		beginSpecified = true;
		begin = i;
		validateBegin();
	}

	/**
	 * @info setEnd
	 * @param int i
	 * @return void 
	 */
	public void setEnd(int i) throws JspTagException {
		endSpecified = true;
		end = i;
		validateEnd();
	}

	/**
	 * @info setStep
	 * @param int i
	 * @return void 
	 */
	public void setStep(int i) throws JspTagException {
		stepSpecified = true;
		step = i;
		validateStep();
	}

	/**
	 * �ڵ�ȭ ������ ���̺� �̸�
	 * @param s
	 */
	public void setKey(String s) {
		key = s;
	}

	/**
	 * table ���� �ڵ忡 �ش��ϴ� �ʵ��̸�
	 * @param codeFld
	 */
	public void setCodeFld(String codeFld) {
		this.codeFld = codeFld;
	}

	/**
	 * table ���� �̸��� �ش��ϴ� �ʵ��̸�
	 * @param nameFld
	 */
	public void setNameFld(String nameFld) {
		this.nameFld = nameFld;
	}

	/**
	 * where ���� : and �� ����
	 * @param whereFld The whereFld to set.
	 */
	public void setWhereFld(String whereFld) {
		this.whereFld = whereFld;
	}
	/**
	 * @param orderFld The orderFld to set.
	 */
	public void setOrderFld(String orderFld) {
		this.orderFld = orderFld;
	}
}