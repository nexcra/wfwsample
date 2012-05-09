/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfAdmMemPayListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemPayListDaoProc ���μ��� ������  
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemPayListDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			String cdhd_id	= data.getString("CDHD_ID");	
			String juminNo	= data.getString("JUMIN_NO");
			String sch_PAY	= data.getString("SCH_PAY");		
			String sch_SERVICE	= data.getString("SCH_SERVICE");		
			String sch_DATE_ST	= data.getString("SCH_DATE_ST");		
			String sch_DATE_ED	= data.getString("SCH_DATE_ED");	
			String sttl_amt		= "0";
			String dc_amt		= "0";
			String norm_amt		= "0";
			
			sch_DATE_ST = GolfUtil.rplc(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.rplc(sch_DATE_ED, "-", "");
			 
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery(cdhd_id, juminNo, sch_PAY, sch_SERVICE, sch_DATE_ST, sch_DATE_ED);    

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("pay_page_no"));
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("pay_page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
										
					sttl_amt = rs.getString("STTL_AMT");
					dc_amt = rs.getString("DC_AMT");
					norm_amt = rs.getString("NORM_AMT");
					if(!GolfUtil.empty(sttl_amt))	sttl_amt = GolfUtil.comma(sttl_amt);
					if(!GolfUtil.empty(dc_amt))		dc_amt = GolfUtil.comma(dc_amt);
					if(!GolfUtil.empty(norm_amt))	norm_amt = GolfUtil.comma(norm_amt);
					
					result.addString("PAY_NM" 			,rs.getString("PAY_NM") );
					result.addString("STTL_AMT" 		,sttl_amt );
					result.addString("REG_DATE" 		,rs.getString("REG_DATE") );
					result.addString("SERVICE_NM"		,rs.getString("SERVICE_NM") );
					
					//2009.10.01 �߰�
					result.addString("DC_AMT"			,dc_amt );
					result.addString("NORM_AMT"			,norm_amt );
					result.addString("CUPN_CTNT"		,rs.getString("CUPN_CTNT") );
					
					result.addString("CARD_NO"			,rs.getString("CARD_NO") );
					
					//2009.12.14 �߰� 
					result.addString("PAY_YN"			,rs.getString("PAY_YN") );
					
					result.addString("RESULT", "00"); //������
				}
			}

			if(result.size() < 1) {
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(String cdhd_id, String juminNo, String sch_PAY, 
    								String sch_SERVICE, String sch_DATE_ST, String sch_DATE_ED){
    	
        StringBuffer sql = new StringBuffer();
     
		sql.append("\n SELECT	*														\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM											\n");
		sql.append("\t 			, CEIL(ROWNUM/?) AS PAGE								\n");
		sql.append("\t 			, MAX(RNUM) OVER() TOT_CNT								\n");
		sql.append("\t 			, ((MAX(RNUM) OVER())-(?-1)*?) AS ART_NUM  			\n");
		
		sql.append("\t 			, PAY_NM, STTL_AMT, REG_DATE, SERVICE_NM		\n");
		sql.append("\t 			, DC_AMT , NORM_AMT , CUPN_CTNT ,CARD_NO, PAY_YN 	\n");
		sql.append("\t 			FROM (										\n");
		//TBGSTTLMGMT(��������)
		sql.append("\t	SELECT ROWNUM RNUM, PAY_NM, STTL_AMT, REG_DATE, SERVICE_NM, DC_AMT, NORM_AMT, CUPN_CTNT, CARD_NO, PAY_YN	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t			SELECT	\n");
		sql.append("\t				T2.GOLF_CMMN_CODE_NM AS PAY_NM, T1.STTL_AMT 	\n");
		sql.append("\t				, TO_CHAR(TO_DATE(SUBSTR(T1.STTL_ATON,1,8)),'YYYY-MM-DD(DY) ')	\n");
		sql.append("\t				||SUBSTR(T1.STTL_ATON,9,2)||':'||SUBSTR(T1.STTL_ATON,11,2)||':'	\n");
		sql.append("\t				||SUBSTR(T1.STTL_ATON,13,2) AS REG_DATE	\n");
		sql.append("\t				, T3.GOLF_CMMN_CODE_NM AS SERVICE_NM, T1.DC_AMT , T1.NORM_AMT , T1.CUPN_CTNT ,T1.CARD_NO 	\n");
		sql.append("\t				, (CASE T1.STTL_STAT_CLSS WHEN 'Y' THEN '�������' ELSE '�����Ϸ�' END) PAY_YN, T1.STTL_ATON ALIGN	 \n");
		sql.append("\t			FROM BCDBA.TBGSTTLMGMT T1	\n");
		sql.append("\t			JOIN BCDBA.TBGCMMNCODE T2	\n");
		sql.append("\t				ON T1.STTL_MTHD_CLSS=T2.GOLF_CMMN_CODE AND T2.GOLF_CMMN_CLSS='0015'	\n");
		sql.append("\t			JOIN BCDBA.TBGCMMNCODE T3	\n");
		sql.append("\t				ON T1.STTL_GDS_CLSS=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0016'	\n");
		sql.append("\t 			WHERE (T1.CDHD_ID='"+cdhd_id+"'	OR T1.CDHD_ID='"+juminNo+"') \n");
		
		if(!GolfUtil.empty(sch_PAY))	sql.append("\t 	AND T1.STTL_MTHD_CLSS='"+sch_PAY+"'	\n");
		if(!GolfUtil.empty(sch_SERVICE))	sql.append("\t 	AND T1.STTL_GDS_CLSS='"+sch_SERVICE+"'	\n");
		if(!GolfUtil.empty(sch_DATE_ST))	sql.append("\t 	AND T1.STTL_ATON>='"+sch_DATE_ST+"000000'	\n");
		if(!GolfUtil.empty(sch_DATE_ED))	sql.append("\t 	AND T1.STTL_ATON<='"+sch_DATE_ED+"000000'	\n");
		
			//�˻����� : ���Ź��=��ü, ���񽺸�=��ü�ΰ��
		if( (GolfUtil.empty(sch_PAY) && GolfUtil.empty(sch_SERVICE)) || 
			//�˻����� : ���Ź��=BCī��,BCī��+TOP����Ʈ, ���񽺸�=��ü�ΰ��
			(!GolfUtil.empty(sch_PAY) &&  (sch_PAY.equals("0001") || sch_PAY.equals("0002")) &&  GolfUtil.empty(sch_SERVICE)) ||
			//�˻����� : ���Ź��=BCī��,BCī��+TOP����Ʈ, ���񽺸�=�����(TM)			
   		    (!GolfUtil.empty(sch_PAY) &&  (sch_PAY.equals("0001") || sch_PAY.equals("0002")) &&  sch_SERVICE.equals("0013")) ||				  
			//�˻����� : ���Ź��=��ü, ���񽺸� =�����TM
			(GolfUtil.empty(sch_PAY) &&  sch_SERVICE.equals("0013"))
		){ 
			
			sql.append("\t			UNION ALL \n");
			//BGLUGANLFEECTNT(�����������ȸ�񳻿�) -> TM�������� (TMȸ���� ��� ��������)
			sql.append("\t			SELECT	\n"); 
			// �Ʒ� DECODE��  TM�����ؼ� �����ڵ忡 ����� ������ �������� ���Ͽ� ������������ ��û���� �Ʒ��� ���� ó�� ��
			sql.append("\t				DECODE(AUTH_CLSS, '1', 'BCī��', '2', 'BCī��+ž����Ʈ', '3', 'BCī��+ž����Ʈ') PAY_NM	\n");
			sql.append("\t				,AUTH_AMT STTL_AMT	\n");
			sql.append("\t				,TO_CHAR(TO_DATE(SUBSTR(AUTH_DATE,1,8)),'YYYY-MM-DD(DY) ')	\n");
			sql.append("\t				||SUBSTR(AUTH_TIME,1,2)||':'||SUBSTR(AUTH_TIME,3,2)||':'	\n");
			sql.append("\t				||SUBSTR(AUTH_TIME,5,2) AS REG_DATE	\n");
			sql.append("\t				,'�����(TM)' SERVICE_NM, 0 DC_AMT,  AUTH_AMT NORM_AMT,  MB_CDHD_NO CUPN_CTNT, CARD_NO	\n");
			sql.append("\t				, DECODE(CNCL_DATE, null, '�����Ϸ�', '�������')  PAY_YN, AUTH_DATE ALIGN	\n");
			sql.append("\t			 FROM BCDBA.TBGLUGANLFEECTNT \n");
			sql.append("\t			 WHERE JUMIN_NO = '"+juminNo+"'\n");
			
			// �Ʒ� ������  TM�����ؼ� �����ڵ忡 ����� ������ �������� ���Ͽ� ������������ ��û���� �Ʒ��� ���� ó�� ��
			if(!GolfUtil.empty(sch_PAY) &&  sch_PAY.equals("0001")){ //BCī��
				sql.append("\t 	AND AUTH_CLSS = '1'	\n");
			}else if(!GolfUtil.empty(sch_PAY) &&  sch_PAY.equals("0002")){ //BCī��+TOP����Ʈ
				sql.append("\t 	AND AUTH_CLSS IN ('2','3')	\n");
			}
			
			if(!GolfUtil.empty(sch_DATE_ST))	sql.append("\t 	AND AUTH_DATE BETWEEN '" + sch_DATE_ST +"'	\n");
			if(!GolfUtil.empty(sch_DATE_ED))	sql.append("\t 	AND '"+sch_DATE_ED +"'	\n");
			
		}
	
		sql.append("\t	)	\n");
		sql.append("\t	ORDER BY ALIGN DESC	\n");
				
		sql.append("\t 			)		\n");
		sql.append("\t 	ORDER BY RNUM	\n");
		sql.append("\t 	)				\n");
		sql.append("\t WHERE PAGE = ?	\n");        

		return sql.toString();
    }
}
