/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntKvpDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ����ȸ > ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.kvp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntKvpDaoProc extends AbstractProc {
	
	public GolfEvntKvpDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int max_num = 0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String socid						= data.getString("socid");
			String social_id_1					= data.getString("social_id_1");
			String name							= data.getString("name");
			String ddd_no						= data.getString("ddd_no");
			String tel_hno						= data.getString("tel_hno");
			String tel_sno						= data.getString("tel_sno");
			String hp_ddd_no					= data.getString("hp_ddd_no");
			String hp_tel_hno					= data.getString("hp_tel_hno");
			String hp_tel_sno					= data.getString("hp_tel_sno");
			String email						= data.getString("email");
			String kvp_idx						= data.getString("idx");
			String sttl_amt						= data.getString("realPayAmt");
			String card_no						= data.getString("CARD_NO");
			String vald_date					= data.getString("VALD_DATE");
			String chkResult					= data.getString("chkResult");		// 4�� ��� ����ȸ���Ⱓ ����ó�� �Ѵ�.
			String payType						= data.getString("payType");	// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
			
			 
			String cdhd_id = "";
			String cdhd_ctgo_seq_no = "";
			String cslt_yn = "N";
			
			//CSLT_YN, CDHD_ID
			
			String golf_cdhd_grd_clss = "";
			String golf_cdhd_grd_bnf = "";
			if(kvp_idx.equals("1")){
				golf_cdhd_grd_clss = "3";
				golf_cdhd_grd_bnf = "5";
			}else if(kvp_idx.equals("2")){
				golf_cdhd_grd_clss = "2";
				golf_cdhd_grd_bnf = "6";
			}else if(kvp_idx.equals("3")){
				golf_cdhd_grd_clss = "1";
				golf_cdhd_grd_bnf = "7";
			}
			
			else if(kvp_idx.equals("4")){
				
				golf_cdhd_grd_clss = "3";
				golf_cdhd_grd_bnf = "5";
				
			}else if(kvp_idx.equals("5")){
				
				golf_cdhd_grd_clss = "2";
				golf_cdhd_grd_bnf = "6";
				
			}else if(kvp_idx.equals("6")){
				
				golf_cdhd_grd_clss = "1";
				golf_cdhd_grd_bnf = "7";
				
			}else if(kvp_idx.equals("7")){
				
				golf_cdhd_grd_clss = "3";
				golf_cdhd_grd_bnf = "5";
				
			}else if(kvp_idx.equals("8")){
				
				golf_cdhd_grd_clss = "2";
				golf_cdhd_grd_bnf = "6";
				
			}else if(kvp_idx.equals("9")){
				
				golf_cdhd_grd_clss = "1";
				golf_cdhd_grd_bnf = "7";
			}				
			
			
			if(chkResult.equals("4")){
				cslt_yn = "Y";
				
				// ȸ���Ⱓ ������ ��� ȸ������ ��������
				pstmt = conn.prepareStatement(getIsMemQuery());
				pstmt.setString(1, socid);
				rs = pstmt.executeQuery();
				if(rs != null && rs.next()){
					cdhd_id = rs.getString("CDHD_ID");
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
				}				
			}

			
			// ��û���̺� idx �������� - getMaxIdxQuery
			pstmt = conn.prepareStatement(getMaxIdxQuery());
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				max_num = rs.getInt("MAX_NUM");
			}
			
						
			// ��û���̺� ��� - ������ ȸ��
			String sql = this.getEvtQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setInt(idx++, max_num);
			pstmt.setString(idx++, socid);
			pstmt.setString(idx++, sttl_amt);
			pstmt.setString(idx++, name);
			pstmt.setString(idx++, ddd_no);
			pstmt.setString(idx++, tel_hno);
			pstmt.setString(idx++, tel_sno);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, email);
			pstmt.setString(idx++, kvp_idx);
			pstmt.setString(idx++, cslt_yn);
			pstmt.setString(idx++, cdhd_id);
			result = pstmt.executeUpdate();
			
			if(result > 0){
				if(chkResult.equals("4")){
					// ����ȸ���Ⱓ ���� ó�� ���ش�.						
					sql = this.getMemPrdExQuery();   
					pstmt = conn.prepareStatement(sql.toString());	
					idx = 1;					
					pstmt.setString(idx++, golf_cdhd_grd_bnf);
					pstmt.setString(idx++, cdhd_id);
					result += pstmt.executeUpdate();
					
					sql = this.getMemGrdUpdQuery();   
					pstmt = conn.prepareStatement(sql.toString());	
					idx = 1;				
					pstmt.setString(idx++, golf_cdhd_grd_bnf);						
					pstmt.setString(idx++, cdhd_id);				
					pstmt.setString(idx++, cdhd_ctgo_seq_no);	
					result += pstmt.executeUpdate();
				}
				else{
					// TM ��� ���̺� �μ�Ʈ ���ش�. getTmInsQuery
					sql = this.getTmInsQuery();   
					pstmt = conn.prepareStatement(sql.toString());
					idx = 1;
					
					pstmt.setString(idx++, "1");				//MB_CDHD_NO - ȸ����ȸ����ȣ
					pstmt.setString(idx++, "4");				//ACPT_CHNL_CLSS - ����ä�� ���� �ڵ�
					pstmt.setString(idx++, name);				//HG_NM
					pstmt.setString(idx++, social_id_1);		//BTHD
					pstmt.setString(idx++, email);				//EMAIL_ID
					pstmt.setString(idx++, ddd_no);				//HOM_DDD_NO
					pstmt.setString(idx++, tel_hno);			//HOM_TEL_HNO
					pstmt.setString(idx++, tel_sno);			//HOM_TEL_SNO
					pstmt.setString(idx++, hp_ddd_no);			//HP_DDD_NO
					pstmt.setString(idx++, hp_tel_hno);			//HP_TEL_HNO
					
					pstmt.setString(idx++, hp_tel_sno);			//HP_TEL_SNO
					pstmt.setString(idx++, "01");				//TB_RSLT_CLSS - TM ��� �����ڵ� 01, ȸ�������ϸ� 00�� ����
	//				pstmt.setString(idx++, "");					//RECP_DATE - �����
					pstmt.setString(idx++, socid);				//JUMIN_NO
					pstmt.setString(idx++, "2");				//RND_CD_CLSS - ����Ʈ ����
					pstmt.setString(idx++, "01");				//JOIN_CHNL - ���Ա��а��
					pstmt.setString(idx++, payType);				//AUTH_CLSS - ���α����ڵ� 1-ī�� ����, 2-���հ���, 3-����Ʈ����
					pstmt.setString(idx++, card_no);			//CARD_NO
					pstmt.setString(idx++, golf_cdhd_grd_clss);	//GOLF_CDHD_GRD_CLSS - ���
					pstmt.setString(idx++, vald_date);			//VALD_LIM - ��ȿ�Ⱓ
					//pstmt.setString(idx++, "5000");				//RCRU_PL_CLSS - ������α����ڵ� 5000->KVP
					pstmt.setString(idx++, "4200");				//RCRU_PL_CLSS - ������α����ڵ� 4200-> KT Olleh Club
					
					result += pstmt.executeUpdate();
				}
				
			}


			if(result > 1) {
				conn.commit();
				result = max_num;
			} else {
				conn.rollback();
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
	

	public int execute_isJoin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			String socid = data.getString("socid");
			String join_chnl = "";
			String cdhd_ctgo_seq_no = "";
			String pay_over = "";
						
			// �̹� ������ ȸ������ �˾ƺ���  
			pstmt = conn.prepareStatement(getIsMemQuery());
			pstmt.setString(1, socid);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				join_chnl = rs.getString("JOIN_CHNL");
				cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
				pay_over = rs.getString("PAY_OVER");
				
				if(join_chnl.equals("4003") && cdhd_ctgo_seq_no.equals("18")){
					// ������� �̺�Ʈ ȸ���ϰ�� ��û�ǵ��� �Ѵ�.
					result = 3;
				}else{
					if(pay_over.equals("Y")){
						// ����ȸ���Ⱓ�� �����ٸ� ����ȸ���Ⱓ �������� ó���Ѵ�.
						result = 4;
					}else{
						result = 1;
					}
				}
			}else{
				// �̹� ��û�� ȸ������ �˾ƺ���
				pstmt = conn.prepareStatement(getIsAplyQuery());
				pstmt.setString(1, socid);
				rs = pstmt.executeQuery();
				if(rs != null && rs.next()){
					result = 2;
				}
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
    * ��û���̺��� max_idx ��������
    ************************************************************************ */
    private String getMaxIdxQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT NVL(MAX(APLC_SEQ_NO),0)+1 MAX_NUM FROM BCDBA.TBGAPLCMGMT	");		
		return sql.toString();
    }


	/** ***********************************************************************
    * �̺�Ʈ ��� 
    ************************************************************************ */
    private String getEvtQuery(){
        StringBuffer sql = new StringBuffer();	
        
		sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT (	");
		sql.append("\n	    APLC_SEQ_NO, GOLF_LESN_RSVT_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, JUMIN_NO, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT	");
		sql.append("\n	    , CO_NM, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, RSVT_CDHD_GRD_SEQ_NO, CSLT_YN, CDHD_ID	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    ?, 1, '1005', 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),? 	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	)	");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * ���� �ֹε�Ϲ�ȣ ȸ���� �ִ��� Ȯ�� 
    ************************************************************************ */
    private String getIsMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT JOIN_CHNL, CDHD_CTGO_SEQ_NO, CDHD_ID	");
		sql.append("\n	, CASE WHEN ACRG_CDHD_END_DATE<TO_DATE(SYSDATE) THEN 'Y' ELSE 'N' END PAY_OVER	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO = ? AND NVL(SECE_YN,'N')<>'Y'	AND CDHD_CTGO_SEQ_NO != '8' ");
		sql.append("\n	ORDER BY MEMBER_CLSS	");
		return sql.toString();
    }

	/** ***********************************************************************
    * ���� �ֹε�Ϲ�ȣ ��û���� �ִ��� Ȯ��
    ************************************************************************ */
    private String getIsAplyQuery(){
        StringBuffer sql = new StringBuffer();	
		//sql.append("\n	SELECT * FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND GREEN_NM='KVP' AND PGRS_YN='Y' AND JUMIN_NO=?	");
		sql.append("\n	SELECT * FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1005' AND PGRS_YN='Y' AND JUMIN_NO=?	");
		return sql.toString();
    }

	/** ***********************************************************************
    * TM ���̺� ����ϱ�
    ************************************************************************ */
    private String getTmInsQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	INSERT INTO BCDBA.TBLUGTMCSTMR 	");
		sql.append("\n	(MB_CDHD_NO, ACPT_CHNL_CLSS, HG_NM, BTHD, EMAIL_ID, HOM_DDD_NO, HOM_TEL_HNO, HOM_TEL_SNO, HP_DDD_NO, HP_TEL_HNO	");
		sql.append("\n	 ,HP_TEL_SNO,TB_RSLT_CLSS,RECP_DATE, JUMIN_NO, RND_CD_CLSS, JOIN_CHNL, AUTH_CLSS, CARD_NO, GOLF_CDHD_GRD_CLSS, VALD_LIM, RCRU_PL_CLSS )	");
		sql.append("\n	VALUES (?,?,?,?,?,?,?,?,?,?	");
		sql.append("\n			,?,?,TO_CHAR(SYSDATE,'YYYYMMDD'),?,?,?,?,?,?,?,?)	");
		return sql.toString();
    }

	/** ***********************************************************************
    * ����ȸ���Ⱓ �����ϱ�
    ************************************************************************ */
    private String getMemPrdExQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET  CDHD_CTGO_SEQ_NO=? 	");
		sql.append("\n	, ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE+365,'YYYYMMDD') 	");
		sql.append("\n	WHERE CDHD_ID=?	");
		return sql.toString();
    }

	/** ***********************************************************************
    * ����ȸ���Ⱓ �����ϱ�
    ************************************************************************ */
    private String getMemGrdUpdQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET CDHD_CTGO_SEQ_NO=? WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? 	");
		return sql.toString();
    }

}
