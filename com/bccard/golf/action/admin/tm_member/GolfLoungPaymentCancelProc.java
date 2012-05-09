/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoungPaymentCancelProc
*   �ۼ���    : ���񽺰����� ������
*   ����      : TMȸ�� ���� ��� Proc 
*   �������  : Golf
*   �ۼ�����  : 2009-07-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
/** ****************************************************************************
 * Media4th 
 * @author
 * @version 2009-05-06
 **************************************************************************** */
public class GolfLoungPaymentCancelProc extends AbstractProc {
	public static final String TITLE = "TM ���� ���";
	/** *****************************************************************
	 * GolfLoungTMListProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfLoungPaymentCancelProc() { 

	}
	
	/** ***********************************************************************
	* Proc ����.
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public TaoResult execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		DbTaoResult result = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {				
			
			//debug("GolfLoungPaymentCancelProc >> ���� ���� >> ");
			
			con = context.getDbConnection("default", null);
			result = new DbTaoResult(TITLE);
			
			String jumin_no		= data.getString("jumin_no");	// 
			String account			= data.getString("account");	//
			String join_chnl			= data.getString("join_chnl");	//
			String work_date			= data.getString("work_date");	//

			long ret =0;
debug("account==>"+account);
			StringBuffer sql = new StringBuffer();	

			sql.append("SELECT  A.HG_NM, A.JUMIN_NO , A.JOIN_CHNL TM_BUZ, \n");
			sql.append("		B.CDHD_ID, B.JOIN_CHNL,A.AUTH_CLSS,A.CARD_NO FMT_CARD_NO,A.VALD_LIM, \n");
			sql.append("		C.AUTH_NO, C.CARD_NO, C.AUTH_AMT, \n");
			sql.append("		(SELECT CDHD_CTGO_SEQ_NO FROM   BCDBA.TBGGOLFCDHDGRDMGMT  WHERE CDHD_ID = B.CDHD_ID ) CDHD_CTGO_SEQ_NO \n");
			sql.append("FROM	BCDBA.TBLUGTMCSTMR  A ,  BCDBA.TBGGOLFCDHD   B   , BCDBA.TBGLUGANLFEECTNT  C \n");
			sql.append("WHERE	A.RND_CD_CLSS='2'     \n");
			sql.append("AND		A.JUMIN_NO =?   \n");
			sql.append("AND		A.TB_RSLT_CLSS IN ('00', '01') \n");
			sql.append("AND		A.RECP_DATE = ? \n");
			sql.append("AND     A.JUMIN_NO = B.JUMIN_NO (+) \n");
			sql.append("AND     A.JUMIN_NO = C.JUMIN_NO (+) \n");
			sql.append("AND     A.RECP_DATE = C.AUTH_DATE (+) \n");

/*
SELECT  A.HG_NM, A.JUMIN_NO , 
		B.CDHD_ID, B.JOIN_CHNL,A.AUTH_CLSS,A.CARD_NO FMT_CARD_NO,A.VALD_LIM, 
		C.AUTH_NO, C.CARD_NO, C.AUTH_AMT,
		(SELECT CDHD_CTGO_SEQ_NO FROM   BCDBA.TBGGOLFCDHDGRDMGMT  WHERE CDHD_ID = B.CDHD_ID ) CDHD_CTGO_SEQ_NO 
FROM	BCDBA.TBLUGTMCSTMR  A ,  BCDBA.TBGGOLFCDHD   B   , BCDBA.TBGLUGANLFEECTNT  C
WHERE	A.RND_CD_CLSS='2'     
AND		A.JUMIN_NO ='7401011903283'   
AND		A.TB_RSLT_CLSS = '01' 
AND		A.RECP_DATE = '20090805'
AND     A.JUMIN_NO = B.JUMIN_NO (+) 
AND     A.JUMIN_NO = C.JUMIN_NO (+)
AND     A.RECP_DATE = C.AUTH_DATE (+)
*/

			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 
    
			pstmt.setString(++idx, jumin_no);
			pstmt.setString(++idx, work_date);
			
			rs = pstmt.executeQuery();
			
			if(rs != null)
			{
debug("rs != null ==>");				
				String cdhd_id = "";
				String grade = "";
				String join_chnl2 = "";
				while(rs.next())  
				{
debug("CDHD_ID ==>"+rs.getString("CDHD_ID"));						
					cdhd_id=rs.getString("CDHD_ID");
					grade=rs.getString("CDHD_CTGO_SEQ_NO");
					join_chnl2 =rs.getString("JOIN_CHNL");

					result.addString("HG_NM",					rs.getString("HG_NM"));
					result.addString("JUMIN_NO"		,rs.getString("JUMIN_NO") );
					result.addString("CDHD_ID",				cdhd_id);
					result.addString("JOIN_CHNL",					join_chnl2);
					result.addString("CDHD_CTGO_SEQ_NO",				grade);


					result.addString("AUTH_CLSS",				rs.getString("AUTH_CLSS"));
					result.addString("CARD_NO",				rs.getString("CARD_NO"));
					result.addString("VALD_LIM",				rs.getString("VALD_LIM"));
					result.addString("AUTH_NO",				rs.getString("AUTH_NO"));
					result.addString("AUTH_AMT",				rs.getString("AUTH_AMT"));
					result.addString("TM_BUZ",				rs.getString("TM_BUZ"));
					
				

					
					if ( cdhd_id==null || "".equals(cdhd_id) ) {
						//�ٷ� ���ó�� ����	
					} else {
						if ( grade==null || "".equals(grade) ) {
							//�ٷ� ���ó�� ����
						} else {
							if ( join_chnl2==null || "".equals(join_chnl2) ) 
							{
								//ȸ������Ÿ ������
								result.addString("RESULT","01");
								result.addString("RESULT_MSG","�ش�ȸ���� ��Ҵ���ڰ� �ƴմϴ�.-����ȸ�����̺� ����Ÿ ������");
								return result;
							} else {
								//TM���ȸ���̸鼭 ��尡 �ƴѰ��(���׷��̵� ȸ��)
								if (  "0003".equals(join_chnl2) ) 	{
									if ( !"7".equals(grade))
									{
										result.addString("RESULT","01");
										result.addString("RESULT_MSG","�ش�ȸ���� ���׷��̵� ȸ���Դϴ�.-��� Ȯ�� �ʿ�");
										return result;
									}
								} else if (  "0002".equals(join_chnl2) ) {
									if ( !"6".equals(grade))
									{
										result.addString("RESULT","01");
										result.addString("RESULT_MSG","�ش�ȸ���� ���׷��̵� ȸ���Դϴ�.-��� Ȯ�� �ʿ�");
										return result;
									}
								} else if (  "0004".equals(join_chnl2) ) {
									if ( !"5".equals(grade))
									{
										result.addString("RESULT","01");
										result.addString("RESULT_MSG","�ش�ȸ���� ���׷��̵� ȸ���Դϴ�.-��� Ȯ�� �ʿ�");
										return result;
									}
								}


							}		
						}
						
					}
					ret++;	
					debug("cdhd_id==>"+cdhd_id);						
				}
			}
												 
debug("ret==>"+ret);		
			if(ret == 0){
				result.addString("RESULT","01"); //��Ҵ���� �ڷ� ����
				result.addString("RESULT_MSG","�ش�ȸ���� ��Ҵ���ڰ� �ƴմϴ�.-TM���� ����Ÿ ����");
			} else {
				int cnt = 0;
				if (account==null || "".equals(account))
				{
					//���̵� ���ٴ°��� TM������ ����Ʈ �α����� ���ߴٴ� �ǹ�
					cnt = 0;
					result.addString("RESULT","00");
	 			} else {
					cnt = getServiceCnt(context,account);
debug("cnt==>"+cnt);					
					if (cnt > 0)
					{
						result.addString("RESULT","01"); //��Ҵ���� �ڷ� ����
						result.addString("RESULT_MSG","�ش�ȸ���� �̹� ����Ͻ� ���񽺰� �ֽ��ϴ�.-��� Ȯ�� �ʿ�");
					} else {
						result.addString("RESULT","00");
					}
				}
			}
						

        } catch(Exception e) {
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." ); 
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}


	/*****************************************************************
	* ���� ��������, �������, ��û����  ��ȸ
	******************************************************************/
	public int getServiceCnt (WaContext context, String account) throws Exception{

		Connection         conn = null;
		PreparedStatement pstmt = null;
		ResultSet            rs = null;	
		int cnt		= 10; 

		try{
			StringBuffer sql = new StringBuffer();	

			sql.append("SELECT SUM(CNT) CNT FROM (  \n");
			sql.append("  SELECT COUNT(*) CNT  FROM BCDBA.TBGSTTLMGMT WHERE STTL_STAT_CLSS ='N' AND CDHD_ID = ? \n");  //���系��  N:�����Ϸ� Y:�������
			sql.append("  UNION ALL  \n");
			sql.append("  SELECT COUNT(*) CNT  FROM BCDBA.TBGRSVTMGMT WHERE RSVT_YN = 'Y' AND CDHD_ID =  ? \n"); //�������  Y:���� N:���
			sql.append("  UNION ALL \n");
			sql.append("  SELECT COUNT(*) CNT  FROM BCDBA.TBGAPLCMGMT WHERE CDHD_ID = ? \n"); //��û����
			sql.append(") \n");

			conn = context.getDbConnection("default", null);

			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, account);
			pstmt.setString(2, account);
			pstmt.setString(3, account);

			rs = pstmt.executeQuery();            

			if (rs.next())	{				
				cnt = rs.getInt(1);					
			}				

		}catch(Exception e){
			info("GolfLoungPaymentCancelProc|getServiceCnt Exceptioin : ", e);
			throw e;

		}finally{
			if(rs != null)    try{ rs.close();    }catch(Exception e){}
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}
		}

		return cnt;
	}



	/**************************************************************
	*  TM�������� update
	**************************************************************/
	public int updateTM(WaContext context, String jumin_no, String work_date ) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;

		int cnt		= 0; 

		try{
			StringBuffer sql = new StringBuffer();	

			sql.append("UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS='98',REJ_RSON ='���ó��-����', SQ3_TCALL_DATE =TO_CHAR(SYSDATE,'yyyyMMdd')  \n");
			sql.append("WHERE RND_CD_CLSS='2'  \n");
			sql.append("AND RECP_DATE= ?   \n");
			sql.append("AND JUMIN_NO=?  \n");
			sql.append("AND ACPT_CHNL_CLSS = '1'  \n");


			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, work_date);
			pstmt.setString(2, jumin_no);
			cnt = pstmt.executeUpdate(); 

		}catch(Exception e){
			info("GolfLoungPaymentCancelProc|updateTM Exceptioin : ", e);
			throw e;
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}			
		}
		return cnt;
	}

	/**************************************************************
	*  ����ȸ�� ���̺� ����
	**************************************************************/
	public int deleteGOLFCDHD(WaContext context, String jumin_no ) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;

		int cnt		= 0; 

		try{
			StringBuffer sql = new StringBuffer();	

			sql.append("DELETE FROM  BCDBA.TBGGOLFCDHD  WHERE JUMIN_NO IN ( ? )  \n");

			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, jumin_no);
			cnt = pstmt.executeUpdate(); 

		}catch(Exception e){
			info("GolfLoungPaymentCancelProc|deleteGOLFCDHD Exceptioin : ", e);
			throw e;
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}			
		}
		return cnt;
	}

	/**************************************************************
	*  ����ȸ�� ��� ���̺� ����
	**************************************************************/
	public int deleteGRD(WaContext context, String account ) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;

		int cnt		= 0; 

		try{
			StringBuffer sql = new StringBuffer();	

			sql.append("DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT  WHERE  CDHD_ID= ?  \n");

			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, account);
			cnt = pstmt.executeUpdate(); 

		}catch(Exception e){
			info("GolfLoungPaymentCancelProc|deleteGOLFCDHD Exceptioin : ", e);
			throw e;
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}			
		}
		return cnt;
	}

	/**************************************************************
	*  ��ȸ�� ���̺� ���� ��ҳ���
	**************************************************************/
	public int updateFee(WaContext context, String jumin_no,String auth_no,String card_no) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;

		int cnt		= 0; 

		try{
			StringBuffer sql = new StringBuffer();	

			sql.append(" UPDATE BCDBA.TBGLUGANLFEECTNT SET  	\n");
			sql.append("  CNCL_DATE =TO_CHAR(SYSDATE,'yyyyMMdd'), CNCL_TIME =TO_CHAR(SYSDATE,'hh24miss') 	 \n");
			sql.append(" WHERE JUMIN_NO = ? 	\n");
			sql.append(" AND  AUTH_NO =? 	\n");
			sql.append(" AND  CARD_NO =? 	\n");
			sql.append(" AND  RND_CD_CLSS = '2' 	\n");

			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, jumin_no);
			pstmt.setString(2, auth_no);
			pstmt.setString(3, card_no);

			cnt = pstmt.executeUpdate(); 

		}catch(Exception e){
			info("GolfLoungPaymentCancelProc|deleteGOLFCDHD Exceptioin : ", e);
			throw e;
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}			
		}
		return cnt;
	}

	

}
