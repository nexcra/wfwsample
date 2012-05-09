/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó��
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0   
******************************************************************************/
public class GolfMemDelFormDaoProc extends AbstractProc {

	public static final String TITLE = "���������ȸ������ó��";

	public GolfMemDelFormDaoProc() {}

	public int execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int cnt = 0;
				
		try {

			// ȸ���������̺� ���� �������� ����
			conn = context.getDbConnection("default", null);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId" ).trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ��춧���� �߰���
			}
			
			sql = possibleCancleYn();
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
        	rs = pstmt.executeQuery();		
        	
			if(rs.next()){
				cnt = rs.getInt("CNT");
			}        	
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return cnt;
	}

	public DbTaoResult getChamp(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId" ).trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ���
			}
			
			// TM ȸ������ �˾ƺ���
			sql = this.getChampQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				//debug("====GolfMemDelActn======JOIN_CHNL========> " + rs.getString("JOIN_CHNL"));
				result.addString("SEQ_NO" 			,rs.getString("SEQ_NO") );
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            

			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
	public DbTaoResult getCardMem(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);
		
		try {
		
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId" ).trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ���
			}			
			// ī��ȸ������ �˾ƺ���
			sql = this.getCardMemQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				//debug("====GolfMemDelActn======JOIN_CHNL========> " + rs.getString("JOIN_CHNL"));
				result.addString("CDHD_SQ2_CTGO" 			,rs.getString("CDHD_SQ2_CTGO") );
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
						
			
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}	
		
		return result;
	}
	
	// TM ��ȭ ���ű� ���� ���� ��������
	public int getTmMovieCnt(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		String juminNo = "";
		int result = 0;
		
		try {
		
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			juminNo = data.getString("juminNo");

			sql = this.getTmMovieCntQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, juminNo );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				result = rs.getInt("CNT");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}	
		
		return result;
	}

	// ����� ��� ��������
	public DbTaoResult getMemGrd(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);
		
		try {
		
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
		 	
			String userId = data.getString("userId" ).trim();		
		
			sql = this.getMemGrdQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){				
				result.addString("CDHD_SQ2_CTGO",rs.getString("CDHD_SQ2_CTGO") );
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}	
		
		return result;
	}	

 	/** ***********************************************************************
	* TM ���� ȸ�� ���̺��� �������� JOIN_CHNL => 2 or 3 �ϰ�� TM
	************************************************************************ */
	private String getJoinChnlQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT JOIN_CHNL, AFFI_FIRM_NM FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* �������ɿ���
	************************************************************************ */
	private String possibleCancleYn(){

		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT COUNT(*)	CNT																											\n");
		//sql.append("\t	SELECT ROWNUM, A.ODR_NO, A.CDHD_ID, A.STTL_MTHD_CLSS, A.STTL_GDS_CLSS, A.STTL_STAT_CLSS, STTL_AMT							\n");
		sql.append("\t	FROM (																														\n");
		sql.append("\t		SELECT PAY.* FROM BCDBA.TBGSTTLMGMT PAY, BCDBA.TBGGOLFCDHD CDHD															\n");
		sql.append("\t		WHERE PAY.CDHD_ID = ? 																									\n");
		sql.append("\t		AND PAY.CDHD_ID = CDHD.CDHD_ID																							\n");
		sql.append("\t		-- ����ô� STTL_ATON����  ACRG_CDHD_JONN_DATE������ ����  �� �����Ƿ� �Ѵ�������  üũ 											\n");
		sql.append("\t		AND PAY.STTL_ATON BETWEEN TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),-1),'YYYYMMDD') AND CDHD.ACRG_CDHD_END_DATE	\n");
		sql.append("\t		AND PAY.STTL_STAT_CLSS = 'N'  AND PAY.STTL_AMT>0																		\n");
		sql.append("\t		--0001:BCī�� 0002:BCī��+TOP����Ʈ 0003:Ÿ��ī�� 0004:Ÿ������ī��															\n");
		sql.append("\t		AND PAY.STTL_MTHD_CLSS IN ('0001','0002','0003','0004')																	\n");
		sql.append("\t		--����� ��																												\n");
		sql.append("\t		AND PAY.STTL_GDS_CLSS IN (																							\n");
		sql.append("\t										SELECT CDHD_SQ2_CTGO																	\n");
		sql.append("\t										FROM BCDBA.TBGGOLFCDHDCTGOMGMT															\n");
		sql.append("\t										WHERE CDHD_SQ1_CTGO = '0002' AND CDHD_SQ2_CTGO != '0004'								\n");
		sql.append("\t		)ORDER BY PAY.ODR_NO DESC																								\n");
		sql.append("\t	) A																															\n");
		sql.append("\t	WHERE ROWNUM = 1																											\n");
		
		return sql.toString();
	}
	
	
	/** ***********************************************************************
	* ī�� ȸ�� ���̺��� �������� JOIN_CHNL => 2 or 3 �ϰ�� TM
	************************************************************************ */
	private String getCardMemQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T4.CDHD_SQ2_CTGO 	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1       	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE T1.CDHD_ID= ?	\n");				
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* ����ǰ�� �����ߴ��� ���� ��������
	************************************************************************ */
	private String getChampQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT SEQ_NO FROM BCDBA.TBGCDHDRIKMGMT WHERE SND_YN='Y' AND CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ȸ�� ����� ��� ��������
	************************************************************************ */
	private String getMemGrdQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T3.CDHD_SQ2_CTGO 	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1       	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T2.CDHD_ID=T1.CDHD_ID	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T3.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE T1.CDHD_ID= ? AND T2.CDHD_CTGO_SEQ_NO IN (5, 6, 7, 8, 11)	\n");				
		sql.append("\t	AND (T1.SECE_YN IS NULL OR T1.SECE_YN = '' OR T1.SECE_YN = 'N')	\n");				
		
		return sql.toString();
	}	
	
	/** ***********************************************************************
	* TM ��ȭ���ű� �̺�Ʈ ���� ���� ���� ��������
	************************************************************************ */
	private String getTmMovieCntQuery(){
		StringBuffer sql = new StringBuffer(); 
		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBEVNTLOTPWIN WHERE SITE_CLSS='10' AND EVNT_NO IN('120','119') AND JUMIN_NO = ?	");
		return sql.toString();
	}	

}
