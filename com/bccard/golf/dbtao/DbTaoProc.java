/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : DbTao 처리를 위한 슈퍼 클래스
* 수정 :
* 내용 :
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
 * DbTao 처리를 위한 슈퍼 클래스.
 * @author 조용국(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
public abstract class DbTaoProc extends AbstractObject implements Serializable {
	protected static final String EMPTY = "";
	protected static final String LF = "\n";
	protected static final String TB = "\t";
	protected static final String UNDEF = "UNDEFINED_ERROR";

    /** ***********************************************************************
    * Proc 실행.
    * @param con Connection
    * @param dataSet 조건정보
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
	* DbTaoException 반환.
	* @param key 메시지키
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(String title, String key) {
		return new DbTaoException( new MsgEtt( MsgEtt.TYPE_ERROR, title, key, null ) );
	}

	/** ***********************************************************************
	* DbTaoException 반환.
	* @param key 메시지키
	* @param t   발생한 Throwalbe
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(String title, String key, Throwable t) {
		return new DbTaoException( new MsgEtt( MsgEtt.TYPE_ERROR, title, key, null ), t );
	}

	/** ***********************************************************************
	* DbTaoException 반환.
	* @param key 메시지키
	* @param t   발생한 Throwalbe
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(MsgEtt ett) {
		return this.getDbTaoException( ett, null );
	}

	/** ***********************************************************************
	* DbTaoException 반환.
	* @param key 메시지키
	* @param t   발생한 Throwalbe
	* @return DbTaoException
	************************************************************************ */
	protected DbTaoException getDbTaoException(MsgEtt ett, Throwable t) {
		return new DbTaoException( ett, t );
	}

}
