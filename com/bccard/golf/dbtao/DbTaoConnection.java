/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 :  2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : 데이터베이스에 연동을 제어한다.
* 적용범위  : welco
* 작성일자  : 2007.01.04
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.sql.Connection;

import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* 데이터베이스에 연동.
* @author  조용국
* @version 2007.01.04
******************************************************************************/
public class DbTaoConnection implements TaoConnection,Serializable {
    /** 내부사용 DB 커넥션 */ private ImplConnection con;

    /** ***********************************************************************
    * 데이터베이스에 연동을 제어한다.
    * @param con Connection
    * @return DbTaoConnection
    ************************************************************************ */
    public DbTaoConnection(Connection con) {
        this.con = new ImplConnection(con);
    }

    /** *****************************************************************
     * ImplConnection.
     * @return ImplConnection
     ***************************************************************** */
	public ImplConnection getImplConnection(){
		return this.con;
	}

    /** *****************************************************************
     * 클래스명과 TaoDataSet 으로 실행.
     * @param data TaoDataSet 형의 자료 데이터
     * @param clazz 실행클래스명
     * @return TaoResult
     ***************************************************************** */
    public TaoResult execute(String clazz, TaoDataSet data) throws TaoException {
        String classname;
        if ( clazz == null ) {
            classname = "TestProc";
        } else {
            classname = clazz;
        }
        DbTaoProc proc = DbTaoProcPool.getProc(classname);
        return proc.execute(this.con, data);
    }

    /** *****************************************************************
     * TaoDataSet 으로 "TestProc" 클래스 실행.
     * @param data TaoDataSet 형의 자료 데이터
     * @return TaoResult
     ***************************************************************** */
    public TaoResult execute(TaoDataSet data) throws TaoException {
        return this.execute(null,data);
    }

    /** *****************************************************************
     * 클래스명으로 실행.
     * @param clazz 실행클래스명
     * @return TaoResult
     ***************************************************************** */
    public TaoResult execute(String clazz) throws TaoException {
        return this.execute(clazz,null);
    }

    /** *****************************************************************
     * Transaction 시작.
     * @param timeout Transaction 타임아웃시간(millisconde)
     ***************************************************************** */
    public void begin(int timeout) throws TaoException {
        this.con.beginTransaction();
    }

    /** *****************************************************************
     * Transaction 롤백.
     ***************************************************************** */
    public void rollback() throws TaoException {
        this.con.rollbackTransaction();
    }

    /** *****************************************************************
     * Transaction 커밋.
     ***************************************************************** */
    public void commit() throws TaoException {
        this.con.commitTransaction();
    }

    /** *****************************************************************
     * AutoCommit 설정(기본값 false).
     * @param truefalse boolean
     ***************************************************************** */
    public void setAutoCommit(boolean truefalse) throws TaoException {
        if ( truefalse ) {
            this.con.stopTransaction();
        } else {
            this.con.beginTransaction();
        }
    }

    /** *****************************************************************
     * AutoCommit 설정조회.
     * @return boolean
     ***************************************************************** */
    public boolean isAutocommit() throws TaoException {
        return !(this.con.isTransaction());
    }

    /** *****************************************************************
     * Transaction 닫기.
     ***************************************************************** */
    public void close() throws TaoException {
        this.con.closeConnection();
    }
}
