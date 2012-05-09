/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemSkyInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��Ű�Ǳ� ���ó��
*   �������  : golf 
*   �ۼ�����  : 2009-12-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfMemSkyInsDaoProc extends AbstractProc {

	public static final String TITLE = "��Ű�Ǳ�ó��";

	public GolfMemSkyInsDaoProc() {}

	/** ***********************************************************************
	* �˾ƺ���    
	*********************************************************************** */
	public int executeSky(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String payType 				= data.getString("payType").trim();		// 1:ī�� 2:ī��+����Ʈ
			String moneyType 			= data.getString("moneyType").trim();	// 1:è�ǿ�(200,000) 2:���(50,000) 3:���(25,000)
			if(GolfUtil.empty(moneyType)){moneyType = "4";}
			String memType 				= data.getString("memType").trim();		// ȸ������ - ��ȸ�� : 1 ��ȸ��:2
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			String insType				= data.getString("insType").trim();		// ���԰�� - TM : 1 �Ϲ� : ""
			//String code					= data.getString("CODE_NO");	//����ó�ڵ�
			String code					= "SKY";	//����ó�ڵ�
			String join_chnl			= data.getString("JOIN_CHNL");	
			
			String strMemClss		= userEtt.getStrMemChkNum();		// ȸ��Clss  2009.10.30 �߰� .getStrMemChkNum()
			
			System.out.print("## GolfMemSkyInsDaoProc | memId : "+memId+" | strMemClss : "+strMemClss+"\n");
			if(!"5".equals(strMemClss)) strMemClss ="1";

			String cdhd_SQ1_CTGO		= "0002";				// cdhd_SQ1_CTGO -> 0001:����ī��� 0002:�������
			String cdhd_SQ2_CTGO		= "000" + moneyType;	// cdhd_SQ2_CTGO -> 0001:è�ǿ� 0002:��� 0003:��� 0004:�Ϲ�
			
			String cdhd_CTGO_SEQ_NO = "";
			
			debug("GolfMemSkyInsDaoProc =============== payType => " + payType);
			debug("GolfMemSkyInsDaoProc =============== moneyType => " + moneyType);
			debug("GolfMemSkyInsDaoProc =============== insType => " + insType);
								            
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
			
			debug("===GolfMemSkyInsDaoProc=======01. �̹� ��ϵ� ȸ������ �˾ƺ���. (��ϵǾ� �ִ� ���̵� �ִ��� �˻�)======");
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
            rs = pstmt.executeQuery();	
			if(!rs.next()){

			}else{
				debug("===GolfMemSkyInsDaoProc=======��ϵ� ���̵� �ִ�============================");

	            sql = this.getMemberUpdateQuery(code);
				pstmt = conn.prepareStatement(sql);	
				pstmt.setString(++idx, join_chnl );
	        	pstmt.setString(++idx, code );
				pstmt.setString(++idx, memId ); 
	        	
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();

				debug("===GolfMemInsDaoProc=======�簡�� ȸ�� : ������̺� - ȸ���з��Ϸù�ȣ ������Ʈ");
	            sql = this.getMemberGradeUpdateQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
	        	pstmt.setString(++idx, memId ); 
	        	
				result = pstmt.executeUpdate();

				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
					
			}
				
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
			
			debug("===GolfMemSkyInsDaoProc=======00. ��Ű�Ǳ��̺�Ʈó�� �Ϸ� ===============");

			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
			
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
	* ��� �˾ƺ���    
	*********************************************************************** */
	public DbTaoResult gradeExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
		
			String sql = this.getMemGradeQuery(); 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("moneyType").trim());
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("memGrade" 	,rs.getString("MEM_GRADE") );
					result.addInt("intMemGrade" 	,rs.getInt("INT_MEM_GRADE") );
					result.addString("RESULT", "00"); //������
					
					debug("MEM_GRADE : " + rs.getString("MEM_GRADE"));
					debug("INT_MEM_GRADE : " + rs.getInt("INT_MEM_GRADE"));
				}
			}else{
				result.addString("RESULT", "01");	
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

 	/** ***********************************************************************
	* �����ϵ� ���̵����� �˾ƺ���    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD		\n");
		sql.append("\t  WHERE JUMIN_NO=?				\n");
		return sql.toString();
	}

   	/** ***********************************************************************
    * ȸ�� �з� ���� �������� - TBGGOLFCDHDCTGOMGMT    
    ************************************************************************ */
    private String getMemberLevelQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("\n");
      sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM					\n");
      sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
      sql.append("\t    WHERE CDHD_SQ1_CTGO=? AND CDHD_SQ2_CTGO=?	\n");
      return sql.toString();
    }

    /** ***********************************************************************
    * ȸ�� ������Ʈ - ����ȸ������    
    ************************************************************************ */
    private String getMemberUpdateQuery(String code){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET												\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')							\n");
 		sql.append("\t		, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')			\n");
		if (!"".equals(code)) {
			sql.append("\t      , JOIN_CHNL= ? , AFFI_FIRM_NM= ?		\n");
		}
 		sql.append("\t		WHERE JUMIN_NO=?															\n");
        return sql.toString();
    }
       
    /** ***********************************************************************
    * ȸ�� ��ް��� ������Ʈ - ����ȸ������    
    ************************************************************************ */
    private String getMemberGradeUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET					\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO=?								\n");
 		sql.append("\t		WHERE CDHD_ID=?									\n");
        return sql.toString();
    }
 
    /** ***********************************************************************
    * ȸ����� �������� 
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT GOLF_CMMN_CODE_NM MEM_GRADE, SUBSTR(GOLF_CMMN_CODE,4,1) INT_MEM_GRADE	\n");
		sql.append("\t	FROM BCDBA.TBGCMMNCODE	\n");
		sql.append("\t	WHERE GOLF_CMMN_CLSS='0005' AND SUBSTR(GOLF_CMMN_CODE,4,1)=?	\n");
        return sql.toString();
    }
}
