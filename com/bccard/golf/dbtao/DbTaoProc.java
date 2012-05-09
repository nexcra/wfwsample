/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : DbTao ó���� ���� ���� Ŭ����
* ���� :
* ���� :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.sql.Connection;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 * DbTao ó���� ���� ���� Ŭ����.
 * @author ���뱹(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
public abstract class DbTaoProc extends AbstractObject implements Serializable {
	protected static final String EMPTY = "";
	protected static final String LF = "\n";
	protected static final String TB = "\t";
	protected static final String UNDEF = "UNDEFINED_ERROR";

    /** ***********************************************************************
    * Proc ����.
    * @param con Connection
    * @param dataSet ��������
    ************************************************************************ */
    public abstract TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException;

	/**
	 * @param errorType
	 * @param title
	 * @param key
	 * @param message
	 * @return
	 */
	protected MsgEtt generateMsgEtt(String errorType
		                            , String title
		                            , String key
		                            , String message) {
		MsgEtt me = new MsgEtt();
		me.setType( errorType );
		if (title   != null ) me.setTitle   ( title   );
		if (key     != null ) me.setKey     ( key     );
		if (message != null ) me.setMessage ( message );
		return me;
	}

	/**
	 * @param title
	 * @param key
	 * @param message
	 * @return
	 */
	protected MsgEtt generateMsgEtt(String title
		                            , String key
		                            , String message) {
		return this.generateMsgEtt(MsgEtt.TYPE_ERROR, title, key, message);
	}

	/** ***********************************************************************
	* DbTaoException ��ȯ.
	* @param key �޽���Ű
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(String title, String key) {
		return new DbTaoException( new MsgEtt( MsgEtt.TYPE_ERROR, title, key, null ) );
	}

	/** ***********************************************************************
	* DbTaoException ��ȯ.
	* @param key �޽���Ű
	* @param t   �߻��� Throwalbe
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(String title, String key, Throwable t) {
		return new DbTaoException( new MsgEtt( MsgEtt.TYPE_ERROR, title, key, null ), t );
	}

	/** ***********************************************************************
	* DbTaoException ��ȯ.
	* @param key �޽���Ű
	* @param t   �߻��� Throwalbe
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(MsgEtt ett) {
		return this.getDbTaoException( ett, null );
	}

	/** ***********************************************************************
	* DbTaoException ��ȯ.
	* @param key �޽���Ű
	* @param t   �߻��� Throwalbe
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(MsgEtt ett, Throwable t) {
		return new DbTaoException( ett, t );
	}

}
