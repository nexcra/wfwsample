/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� :  2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : �����ͺ��̽��� ������ �����Ѵ�.
* �������  : welco
* �ۼ�����  : 2007.01.04
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;
import java.sql.Connection;

import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* �����ͺ��̽��� ����.
* @author  ���뱹
* @version 2007.01.04
******************************************************************************/
public class DbTaoConnection implements TaoConnection,Serializable {
    /** ���λ�� DB Ŀ�ؼ� */ private ImplConnection con;

    /** ***********************************************************************
    * �����ͺ��̽��� ������ �����Ѵ�.
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
     * Ŭ������� TaoDataSet ���� ����.
     * @param data TaoDataSet ���� �ڷ� ������
     * @param clazz ����Ŭ������
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
     * TaoDataSet ���� "TestProc" Ŭ���� ����.
     * @param data TaoDataSet ���� �ڷ� ������
     * @return TaoResult
     ***************************************************************** */
    public TaoResult execute(TaoDataSet data) throws TaoException {
        return this.execute(null,data);
    }

    /** *****************************************************************
     * Ŭ���������� ����.
     * @param clazz ����Ŭ������
     * @return TaoResult
     ***************************************************************** */
    public TaoResult execute(String clazz) throws TaoException {
        return this.execute(clazz,null);
    }

    /** *****************************************************************
     * Transaction ����.
     * @param timeout Transaction Ÿ�Ӿƿ��ð�(millisconde)
     ***************************************************************** */
    public void begin(int timeout) throws TaoException {
        this.con.beginTransaction();
    }

    /** *****************************************************************
     * Transaction �ѹ�.
     ***************************************************************** */
    public void rollback() throws TaoException {
        this.con.rollbackTransaction();
    }

    /** *****************************************************************
     * Transaction Ŀ��.
     ***************************************************************** */
    public void commit() throws TaoException {
        this.con.commitTransaction();
    }

    /** *****************************************************************
     * AutoCommit ����(�⺻�� false).
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
     * AutoCommit ������ȸ.
     * @return boolean
     ***************************************************************** */
    public boolean isAutocommit() throws TaoException {
        return !(this.con.isTransaction());
    }

    /** *****************************************************************
     * Transaction �ݱ�.
     ***************************************************************** */
    public void close() throws TaoException {
        this.con.closeConnection();
    }
}
