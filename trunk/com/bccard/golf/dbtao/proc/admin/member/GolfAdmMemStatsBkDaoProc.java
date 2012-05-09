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
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0  
 ******************************************************************************/
public class GolfAdmMemStatsBkDaoProc extends AbstractProc {
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ��ŷ���";
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc ���μ��� ������   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemStatsBkDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request, TaoDataSet dataSet) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			conn = context.getDbConnection("default", null);
			
			String type	= dataSet.getString("type").trim(); 
			String type_nm	= dataSet.getString("type_nm").trim(); 
			String sql = ""; 

			if("nm".equals(type)){
				sql = this.getNmQuery();
			}else if("par3".equals(type)){
				sql = this.getPar3Query();
			}else if("range".equals(type)){
				sql = this.getRangeQuery();
			}else if("duns".equals(type)){
				sql = this.getDunsQuery();
			}else if("jeju".equals(type)){
				sql = this.getJejuQuery();
			}else if("ls".equals(type)){
				sql = this.getLsQuery();
			}else if("vip".equals(type)){
				sql = this.getVipQuery();
			}else if("gr".equals(type)){
				sql = this.getGrQuery();
			}

			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			String yn = "";
			String mn = "";
			String grd = "";
			String gr = "";
			String type_col = "";

			if(rs != null) {			 

				while(rs.next())  {
					type_col = "com";
					mn = rs.getString("MN");
					grd = rs.getString("GRD");
					if("par3".equals(type) || "vip".equals(type)){
						gr = rs.getString("GR");
					}
					if(!("jeju".equals(type) || "ls".equals(type))){
						yn = rs.getString("YN");
					}
					
					if("jeju".equals(type) || "ls".equals(type)){
						if(GolfUtil.empty(grd)){
							if(GolfUtil.empty(mn)){
								grd = type_nm + " �հ�";
							}else{
								grd = mn + " �հ�";
							}
							type_col = "sum";
						}
					}else{
						if(GolfUtil.empty(yn)){
							if(GolfUtil.empty(mn)){
								yn = type_nm + " �հ�";
							}else if(GolfUtil.empty(grd)){
								yn = mn + " �հ�";
							}else if(GolfUtil.empty(gr)){
								yn = mn + "�� " + grd + " �հ�";
							}else if(GolfUtil.empty(yn)){
								yn = mn + "�� " + grd + " " + gr + " �հ�";
							}
							type_col = "sum";
						}
					}

					result.addString("MN",		mn);
					result.addString("GRD",		grd);
					result.addString("GR",		gr);
					result.addString("YN",		yn);
					result.addString("CNT",		rs.getString("CNT"));
					result.addString("CTYPE",	type_col);
				}
				
				result.addString("RESULT","00");
				
			}else{
				result.addString("RESULT","01");
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
    * �Ϲݺ�ŷ   
    ************************************************************************ */
    private String getNmQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, YN, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(APL.REG_ATON,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM GRD	\n");
		sql.append("\t	    , CASE APL.PGRS_YN WHEN 'Y' THEN '��û' ELSE '���' END AS YN	\n");
		sql.append("\t	    FROM BCDBA.TBGAPLCMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON APL.CDHD_ID=CDHD.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE APL.GOLF_SVC_APLC_CLSS IN ('0006','0007')	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD, YN)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9), YN	\n");

		return sql.toString();
    }

	/** ***********************************************************************
    * ��3 ��ŷ 
    ************************************************************************ */
    private String getPar3Query(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, GR, YN, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(APL.ROUND_HOPE_DATE,1,6) AS MN, GR.GREEN_NM AS GR	\n");
		sql.append("\t	    , CODE.GOLF_CMMN_CODE_NM AS GRD	\n");
		sql.append("\t	    , CASE RSVT_YN WHEN 'Y' THEN '��û' ELSE '���' END YN	\n");
		sql.append("\t	    FROM BCDBA.TBGRSVTMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGAFFIGREEN GR ON APL.AFFI_GREEN_SEQ_NO=GR.AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON CDHD.CDHD_ID=APL.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='P'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD, GR, YN)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9), GR, YN DESC	\n");

		return sql.toString();
    }

	/** ***********************************************************************
    * SKY72 �帲����������   
    ************************************************************************ */
    private String getRangeQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, YN, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(DY.RSVT_ABLE_DATE,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM AS GRD	\n");
		sql.append("\t	    , CASE RSVT_YN WHEN 'Y' THEN '��û' ELSE '���' END YN	\n");
		sql.append("\t	    FROM BCDBA.TBGRSVTMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON CDHD.CDHD_ID=APL.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT TME ON TME.RSVT_ABLE_BOKG_TIME_SEQ_NO = APL.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT DY ON TME.RSVT_ABLE_SCD_SEQ_NO=DY.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='D'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD, YN)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9), YN	\n");
        
		return sql.toString();
    }

	/** ***********************************************************************
    * SKY72 �帲�ὺ  
    ************************************************************************ */
    private String getDunsQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, YN, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(DY.BOKG_ABLE_DATE,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM AS GRD	\n");
		sql.append("\t	    , CASE RSVT_YN WHEN 'Y' THEN '��û' ELSE '���' END YN	\n");
		sql.append("\t	    FROM BCDBA.TBGRSVTMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON CDHD.CDHD_ID=APL.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT TME ON TME.RSVT_ABLE_BOKG_TIME_SEQ_NO = APL.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT DY ON TME.RSVT_ABLE_SCD_SEQ_NO=DY.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='S'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD, YN)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9), YN	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * ���ְ�������   
    ************************************************************************ */
    private String getJejuQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT	\n");
		sql.append("\t	    SUBSTR(APL.ROUND_HOPE_DATE,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM AS GRD	\n");
		sql.append("\t	    FROM BCDBA.TBGRSVTMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON CDHD.CDHD_ID=APL.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='J'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9)	\n");
        
		return sql.toString();
    }

	/** ***********************************************************************
    * ����
    ************************************************************************ */
    private String getLsQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(APL.REG_ATON,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM GRD	\n");
		sql.append("\t	    FROM BCDBA.TBGAPLCMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON APL.CDHD_ID=CDHD.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE APL.GOLF_SVC_APLC_CLSS='0001'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9)	\n");

		return sql.toString();
    }

	/** ***********************************************************************
    * VIP ��ŷ
    ************************************************************************ */
    private String getVipQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, GR, YN, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(DY.BOKG_ABLE_DATE,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM GRD	\n");
		sql.append("\t	    , GR.GREEN_NM AS GR, CASE RSVT_YN WHEN 'Y' THEN '��û' ELSE '���' END YN	\n");
		sql.append("\t	    FROM BCDBA.TBGRSVTMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON CDHD.CDHD_ID=APL.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT TME ON TME.RSVT_ABLE_BOKG_TIME_SEQ_NO = APL.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT DY ON TME.RSVT_ABLE_SCD_SEQ_NO=DY.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGAFFIGREEN GR ON APL.AFFI_GREEN_SEQ_NO=GR.AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='M'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD, GR, YN)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9), GR, YN DESC	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * �׸������� 
    ************************************************************************ */
    private String getGrQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT MN, GRD, YN, COUNT(*) CNT	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT SUBSTR(APL.REG_ATON,1,6) AS MN, CODE.GOLF_CMMN_CODE_NM GRD	\n");
		sql.append("\t	    , CASE APL.PGRS_YN WHEN 'Y' THEN '��û' ELSE '���' END AS YN	\n");
		sql.append("\t	    FROM BCDBA.TBGAPLCMGMT APL	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHD CDHD ON APL.CDHD_ID=CDHD.CDHD_ID	\n");
		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON CDHD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE CODE ON CTGO.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t	    WHERE APL.GOLF_SVC_APLC_CLSS ='0008'	\n");
		sql.append("\t	)	\n");
		sql.append("\t	GROUP BY ROLLUP(MN, GRD, YN)	\n");
		sql.append("\t	ORDER BY MN, DECODE(GRD, 'Champion', 1, 'Black', 2, 'Blue', 3, 'Gold', 4, 'White', 5, 'NHƼŸ��', 6	\n");
		sql.append("\t	, 'NH�÷�Ƽ��', 7, '������������', 8, '���� ���� �÷�Ƽ��', 9), YN	\n");

		return sql.toString();
    }
}
