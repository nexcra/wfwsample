/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : 트란잭션 주체를 제어하는 내부 사용 Connection 클래스
* 수정 :
* 내용 :
******************************************************************************/
package com.bccard.golf.dbtao;


import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
//import java.sql.NClob;
//import java.sql.SQLClientInfoException;
//import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLWarning;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
 * 트란잭션 주체를 제어하는 내부 사용 Connection 클래스.
 * @author 조용국(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
class ImplConnection implements Connection, Serializable {

    /**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	/** 원본 SQL커넥션       */ private Connection connection;
    /** 트란잭션 모드 설정값 */ private boolean transaction;

    /** ***********************************************************************
    * 트란잭션 주체를 제어하는 내부 사용 Connection 클래스.
    * @param con Connection
    ************************************************************************ */
    ImplConnection(Connection con) {
        this.connection = con;
        try { stopTransaction(); } catch(Throwable ignore) {}
    }

    /** ***********************************************************************
    * 예외를 발생하기 위한 메서드.
    * @ param msg 예외메시지
    * @ param t 예외가 발생한 원본 Throwable
    ************************************************************************ */
    private DbTaoException errorHandler(String msg,Throwable t) {
        MsgEtt msgEtt = new MsgEtt();
        msgEtt.setType( MsgEtt.TYPE_ERROR );
        msgEtt.setTitle("데이터베이스 오류");
        msgEtt.setMessage( msg );
        if ( t == null ) {
            return new DbTaoException(msgEtt);
        } else {
            return new DbTaoException(msgEtt,t);
        }
    }

    /**************************************************************************
    * 트란잭션 시작.
    **************************************************************************/
    void beginTransaction() throws DbTaoException {
        try {
            this.connection.setAutoCommit(false);
            this.transaction = true;
        } catch(Throwable t) {
            throw errorHandler("트란잭션 시작 실패.",t);
        }
    }

    /**************************************************************************
    * 트란잭션 종료.
    **************************************************************************/
    void stopTransaction() throws DbTaoException {
        try {
            this.connection.setAutoCommit(true);
            this.transaction = false;
        } catch(Throwable t) {
            throw errorHandler("트란잭션 종료 실패.",t);
        }
    }

    /**************************************************************************
    * 트란잭션 여부 반환.
    * @return 트란잭션여부
    **************************************************************************/
    boolean isTransaction() { return this.transaction; }

    /**************************************************************************
    * 트란잭션 커밋.
    **************************************************************************/
    void commitTransaction() throws DbTaoException {
        try {
            this.connection.commit();
        } catch(Throwable t) {
            throw errorHandler("트란잭션 커밋 실패.",t);
        }
    }

    /**************************************************************************
    * 트란잭션 롤백.
    **************************************************************************/
    void rollbackTransaction() throws DbTaoException {
        try {
            this.connection.rollback();
        } catch(Throwable t) {
            throw errorHandler("트란잭션 롤백 실패.",t);
        }
    }

    /**************************************************************************
    * 커넥션 닫기.
    **************************************************************************/
    void closeConnection() throws DbTaoException {
        try {
            stopTransaction();
            this.connection.close();
        } catch(Throwable t) {
            throw errorHandler("커넥션 종료 실패.",t);
        }
    }

    /** ***********************************************************************
    * 커넥션닫기. 여기서는 아무 처리도 하지 않는다.
    ************************************************************************ */
    public void close() throws SQLException {

    }

    /** ***********************************************************************
    * 트란잭션 모드가 false일때만 commit한다.
    ************************************************************************ */
    public void commit() throws SQLException {
        if ( ! this.transaction ) {
            this.connection.commit();
        }
    }

    /** ***********************************************************************
    * 트란잭션 모드가 false일때만 rollback한다.
    ************************************************************************ */
    public void rollback() throws SQLException {
        if ( ! this.transaction ) {
            this.connection.rollback();
        }
    }

    /** ***********************************************************************
    * 트란잭션 모드가 false일때만 동작한다.
    ************************************************************************ */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if ( ! this.transaction ) {
            this.connection.setAutoCommit(autoCommit);
        }
    }

    /** ***********************************************************************
    * 트란잭션 모드가 false일때만 동작한다.
    ************************************************************************ */
    public boolean getAutoCommit() throws SQLException {
        if ( ! this.transaction ) {
            return this.connection.getAutoCommit();
        } else {
            return true;
        }
    }

    /** java.sql.Connection
     * @see java.sql.Connection#createStatement()
     */
    public Statement createStatement() throws SQLException { return this.connection.createStatement(); }
    /** java.sql.Connection
     * @see java.sql.Connection#createStatement(int, int)
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return this.connection.createStatement(resultSetType,resultSetConcurrency); }
	/** java.sql.Connection
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency,int resultSetHoldability) throws SQLException { return this.connection.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException { return this.connection.prepareStatement(sql); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return this.connection.prepareStatement(sql,autoGeneratedKeys); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException { return this.connection.prepareStatement(sql,columnNames); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException { return this.connection.prepareStatement(sql,columnIndexes); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return this.connection.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return this.connection.prepareStatement(sql,resultSetType,resultSetConcurrency); }
    /** java.sql.Connection
     * @see java.sql.Connection#prepareCall(java.lang.String)
     */
    public CallableStatement prepareCall(String sql) throws SQLException { return this.connection.prepareCall(sql); }
    /** java.sql.Connection
     * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
     */
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return this.connection.prepareCall(sql,resultSetType,resultSetConcurrency); }
	/** java.sql.Connection
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql,int resultSetType,int resultSetConcurrency,int resultSetHoldability) throws SQLException { return this.connection.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);}
	/** java.sql.Connection
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException { this.connection.clearWarnings();}
    /** java.sql.Connection
     * @see java.sql.Connection#getCatalog()
     */
    public String getCatalog() throws SQLException {  return this.connection.getCatalog();}
    /** java.sql.Connection
     * @see java.sql.Connection#getMetaData()
     */
    public DatabaseMetaData getMetaData() throws SQLException {  return this.connection.getMetaData();}
    /** java.sql.Connection
     * @see java.sql.Connection#getTransactionIsolation()
     */
    public int getTransactionIsolation() throws SQLException {  return this.connection.getTransactionIsolation();}
    /** java.sql.Connection
     * @see java.sql.Connection#getTypeMap()
     */
    public Map getTypeMap() throws SQLException {  return this.connection.getTypeMap();}
    /** java.sql.Connection
     * @see java.sql.Connection#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException {  return this.connection.getWarnings();}
    /** java.sql.Connection
     * @see java.sql.Connection#isClosed()
     */
    public boolean isClosed() throws SQLException { return this.connection.isClosed();  }
    /** java.sql.Connection
     * @see java.sql.Connection#isReadOnly()
     */
    public boolean isReadOnly() throws SQLException {  return this.connection.isReadOnly();}
    /** java.sql.Connection
     * @see java.sql.Connection#nativeSQL(java.lang.String)
     */
    public String nativeSQL(String sql) throws SQLException {  return this.connection.nativeSQL(sql);}
    /** java.sql.Connection
     * @see java.sql.Connection#setCatalog(java.lang.String)
     */
    public void setCatalog(String catalog) throws SQLException {  this.connection.setCatalog(catalog);}
    /** java.sql.Connection
     * @see java.sql.Connection#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) throws SQLException {  this.connection.setReadOnly(readOnly);}
    /** java.sql.Connection
     * @see java.sql.Connection#setTransactionIsolation(int)
     */
    public void setTransactionIsolation(int level) throws SQLException {  this.connection.setTransactionIsolation(level);}
	/** java.sql.Connection
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException { return this.connection.setSavepoint();}
	/** java.sql.Connection
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException { return this.connection.setSavepoint(name);}
	/** java.sql.Connection
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map map) throws SQLException {  this.connection.setTypeMap(map);}
	/** java.sql.Connection
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {  this.connection.rollback(savepoint);}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {  this.connection.releaseSavepoint(savepoint); }
	/** java.sql.Connection
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException  {  this.connection.setHoldability(holdability); }
	/** java.sql.Connection
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException  { return this.connection.getHoldability();}

	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	//public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
	//	return null;
	//}

	//public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
	//	return null;
	//}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public String getClientInfo(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public boolean isValid(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	//public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	//}

	//public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	//}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	/** java.sql.Connection
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

//	public NClob createNClob() throws SQLException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public SQLXML createSQLXML() throws SQLException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void setClientInfo(String arg0, String arg1)
//			throws SQLClientInfoException {
//		// TODO Auto-generated method stub
//		
//	}
}