/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmWorldPrimeUpdDaoProc
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ > ���������� ����
*   �������  : Topn
*   �ۼ�����  : 2010-09-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  BC Golf
 * @author	Media4th
 * @version 1.0
 **************************************************************************** */
public class GolfAdmWorldPrimeUpdDaoProc extends DbTaoProc {

	/**
	 * Proc ����. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt				= null;
		ResultSet rs 						= null;
		String title						= dataSet.getString("title");
		String actnKey 						= null;
		DbTaoResult result					= new DbTaoResult(title);
		String strMESSAGE_KEY 				= "GolfTao_Common_reg";
		
		try {
		
			actnKey 						= dataSet.getString("actnKey");
			int res 						= 0;
			int pidx 						= 0;
			//��ȸ ����			
			String mode						= dataSet.getString("mode"); 			// ó������
						
			if("upd".equals(mode)) 
			{
				con.setAutoCommit(false);
				
				if(!"".equals(dataSet.getString("aplc_seq_no")) && dataSet.getString("aplc_seq_no") != null )
				{
					pstmt = con.prepareStatement(getUpdateQuery(dataSet));
					pidx = 0;					
					
					
					if(!"".equals(dataSet.getString("pu_date2"))) pstmt.setString(++pidx, dataSet.getString("pu_date2")); // PGRS_YN
					if(!"".equals(dataSet.getString("pgrs_yn"))) pstmt.setString(++pidx, dataSet.getString("pgrs_yn")); // PGRS_YN
					if(!"".equals(dataSet.getString("payType"))) pstmt.setString(++pidx, dataSet.getString("payType")); // CSLT_YN
					
					if(!"".equals(dataSet.getString("memJoinType")))
					{
						pstmt.setString(++pidx, dataSet.getString("memJoinType"));	// LESN_SEQ_NO
						if("1".equals(dataSet.getString("memJoinType")))
						{
							pstmt.setString(++pidx, "1000000");						// STTL_AMT
						}
						else if("2".equals(dataSet.getString("memJoinType")))
						{
							pstmt.setString(++pidx, "2000000");
						}
					}
					if(!"".equals(dataSet.getString("memo_expl"))) 
					{
						pstmt.setString(++pidx, dataSet.getString("memo_expl"));
					}
					
					
					pstmt.setString(++pidx, dataSet.getString("adminId"));					
					pstmt.setString(++pidx, dataSet.getString("aplc_seq_no"));										
					res = pstmt.executeUpdate();
				
					if(!"".equals(dataSet.getString("coNm")))	//�ֹ���ȣ�� �ִ���
					{
						//�������� �Է�
						if("2".equals(dataSet.getString("payType")))	//�ݼ��ͽ���
						{
							//�̹� �ֹ���ȣ�� ���������� �����ϴ��� üũ
							pstmt = con.prepareStatement(getSttlSelectQuery());
							pidx = 0;
							pstmt.setString(++pidx, dataSet.getString("coNm"));
							rs = pstmt.executeQuery();
							
							if ( rs.next() )
							{
								
								//UPDATE
								pstmt = con.prepareStatement(getSttlUpdQuery(dataSet.getString("pgrs_yn")));	
								pidx = 0;
								pstmt.setString(++pidx, dataSet.getString("callCardNo"));
								pstmt.setString(++pidx, dataSet.getString("callIns"));
								pstmt.setString(++pidx, dataSet.getString("callSttlAmt"));
								pstmt.setString(++pidx, dataSet.getString("coNm"));
								pstmt.executeUpdate();
								
							}
							else
							{
								//INSERT
								pstmt = con.prepareStatement(getSttlInsQuery(dataSet.getString("pgrs_yn")));	
								pidx = 0;
								pstmt.setString(++pidx, dataSet.getString("coNm"));
								pstmt.setString(++pidx, dataSet.getString("callCardNo"));
								pstmt.setString(++pidx, dataSet.getString("callIns"));
								pstmt.setString(++pidx, dataSet.getString("callSttlAmt"));
								pstmt.setString(++pidx, dataSet.getString("cdhdID"));
								pstmt.executeUpdate();
							}
							
						}
						else if("3".equals(dataSet.getString("payType"))) //������ü
						{
							//�̹� �ֹ���ȣ�� ���������� �����ϴ��� üũ
							pstmt = con.prepareStatement(getSttlSelectQuery());
							pidx = 0;
							pstmt.setString(++pidx, dataSet.getString("coNm"));
							rs = pstmt.executeQuery();
							if ( rs.next() )
							{
								//UPDATE
								pstmt = con.prepareStatement(getSttlPay3UpdQuery(dataSet.getString("pgrs_yn")));	
								pidx = 0;
								pstmt.setString(++pidx, dataSet.getString("pay3Name")+"000000");
								pstmt.setString(++pidx, dataSet.getString("pay3Amt"));
								pstmt.setString(++pidx, dataSet.getString("coNm"));
								pstmt.executeUpdate();
								
							}
							else
							{
								//INSERT
								pstmt = con.prepareStatement(getSttlPay3InsQuery(dataSet.getString("pgrs_yn")));	
								pidx = 0;
								pstmt.setString(++pidx, dataSet.getString("coNm"));
								pstmt.setString(++pidx, dataSet.getString("pay3Amt"));
								pstmt.setString(++pidx, dataSet.getString("cdhdID"));
								pstmt.setString(++pidx, dataSet.getString("pay3Name")+"000000");								
								pstmt.executeUpdate();
								
							}
							
						}
						else if("1".equals(dataSet.getString("payType"))) //�¶��ΰ���
						{
							pstmt = con.prepareStatement(getSttlPay1UpdQuery(dataSet.getString("pgrs_yn")));	
							pidx = 0;								
							pstmt.setString(++pidx, dataSet.getString("coNm"));
							pstmt.executeUpdate();
						
							
							
						}
						
						
					}
					
					
					
					
				}
				
					
											
				
			}
			else if("updBooking".equals(mode))
			{
				pstmt = con.prepareStatement(getChgEvnt(dataSet));
				pidx = 0;		
				pstmt.setString(++pidx, dataSet.getString("evntPgrsClss"));
				pstmt.setString(++pidx, dataSet.getString("note"));
				pstmt.setString(++pidx, dataSet.getString("mgrMemo"));
				pstmt.setString(++pidx, dataSet.getString("hadc_num"));
				pstmt.setString(++pidx, dataSet.getString("cus_rmrk"));	
				pstmt.setString(++pidx, dataSet.getString("rsvt_date").replaceAll("-", ""));
				pstmt.setString(++pidx, dataSet.getString("aplc_seq_no"));
				
				res = pstmt.executeUpdate();
				
				if(!"0".equals(dataSet.getString("comp_num")))
				{
					// ������ ����  
					pstmt = con.prepareStatement(getDelComp());
					pidx = 0;		
					pstmt.setString(++pidx, dataSet.getString("aplc_seq_no"));
					pstmt.executeUpdate();
					int idx = 0;	
					// ������ ��� - ������
					for(int i=1; i<4; i++){
						if(!GolfUtil.empty(dataSet.getString("comp_bkg_pe_nm_"+i))){  
							
							pstmt = con.prepareStatement(getEvtCompnQuery());
														
							idx = 1;	
							pstmt.setInt(idx++, Integer.parseInt(dataSet.getString("aplc_seq_no")));							
							pstmt.setInt(idx++, i+2);
							
							pstmt.setString(idx++, "2");
							pstmt.setString(idx++, dataSet.getString("comp_bkg_pe_nm_"+i));
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.executeUpdate();
							
						}
					}
					
					
					
				}
				
				
				
				
				
				
				
				
				
			}			
			else if("del".equals(mode))
			{
				
				pstmt = con.prepareStatement(getDeleteQuery());
				pidx = 0;					
				pstmt.setString(++pidx, dataSet.getString("p_idx"));
				res = pstmt.executeUpdate();
				
			}
			else if("pic_del".equals(mode))
			{				
				pstmt = con.prepareStatement(getImgDelQuery(dataSet.getString("img_del_type")));
				pidx = 0;									
				if("IMG_NM".equals(dataSet.getString("img_del_type")))
				{					
					pstmt.setString(++pidx, dataSet.getString("img_nm"));
				}								
				pstmt.setString(++pidx, dataSet.getString("p_idx"));
				res = pstmt.executeUpdate();
			}
			
			if(res>0) {
				result.addString("RESULT","00");
				con.commit();
			}
			else {
				result.addString("RESULT","01");
				con.rollback();
			}
			
		} catch(Exception e){
			// Ʈ������ �����϶��� �ѹ�
			try {
				if (!con.getAutoCommit()) {
					con.rollback();
				}
			} catch (Exception ex) {}
			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, strMESSAGE_KEY, null );
			msgEtt.addEvent( actnKey + ".do", "/topn/img/town/images/office/bt_ok.gif");
			throw new DbTaoException(msgEtt,e);
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		return result;
			
		
	}	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSttlPay1UpdQuery(String pgrs_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGSTTLMGMT SET								");
		if("C".equals(pgrs_yn))
		{
			sql.append("\n	CNCL_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')												");
		}
		else
		{
			sql.append("\n	CNCL_ATON = ''												");
		}
				
		sql.append("\n	WHERE ODR_NO = ? 			");	
		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSttlPay3InsQuery(String pgrs_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGSTTLMGMT 							");
		sql.append("\n	( ODR_NO , STTL_AMT, CDHD_ID, CNCL_ATON, STTL_ATON )												");
		sql.append("\n	VALUES											");
		sql.append("\n	( ?, ?, ?, 								");
		if("C".equals(pgrs_yn))
		{
			sql.append("\n	TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	,			");
		}
		else
		{
			sql.append("\n	''	,			");
		}
		sql.append("\n	? )									");
		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSttlPay3UpdQuery(String pgrs_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGSTTLMGMT SET								");
		sql.append("\n	STTL_ATON = ?	,												");
		if("C".equals(pgrs_yn))
		{
			sql.append("\n	CNCL_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	,											");
		}
		else
		{
			sql.append("\n	CNCL_ATON = ''	,											");
		}
		sql.append("\n	STTL_AMT = ?												");		
		sql.append("\n	WHERE ODR_NO = ? 			");	
		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSttlInsQuery(String pgrs_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGSTTLMGMT 							");
		sql.append("\n	( ODR_NO , CARD_NO, INS_MCNT, STTL_AMT, CDHD_ID, CNCL_ATON, STTL_ATON )												");
		sql.append("\n	VALUES											");
		sql.append("\n	( ?, ?, ?, ?, ?, 								");
		if("C".equals(pgrs_yn))
		{
			sql.append("\n	TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	,			");
		}
		else
		{
			sql.append("\n	''	,			");
		}
		sql.append("\n	TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') )			");
		
		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSttlUpdQuery(String pgrs_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGSTTLMGMT SET								");
		sql.append("\n	CARD_NO = ?	,												");
		sql.append("\n	INS_MCNT = ?	,											");
		if("C".equals(pgrs_yn))
		{
			sql.append("\n	CNCL_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	,											");
		}
		else
		{
			sql.append("\n	CNCL_ATON = ''	,											");
		}
		sql.append("\n	STTL_AMT = ?												");
		sql.append("\n	WHERE ODR_NO = ? 			");	
		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSttlSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT ODR_NO FROM BCDBA.TBGSTTLMGMT	 							");
		sql.append("\n	WHERE ODR_NO = ? 				");	
		return sql.toString();
	}	
	/** ***********************************************************************
	* ������ ���
	************************************************************************ */
	private String getEvtCompnQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLCPE (	");
		sql.append("\n	    SITE_CLSS, APLC_SEQ_NO, SEQ_NO, GOLF_SVC_APLC_CLSS, APLC_PE_CLSS, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID, JUMIN_NO, CDHD_GRD_SEQ_NO	");
		sql.append("\n	    , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, STTL_STAT_CLSS, CNCL_YN	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    '2', ?, ?, '1003', ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, '0', 'N'	");
		sql.append("\n	)	");
		return sql.toString();
	}
	/** ***********************************************************************
	* ������ ����
	************************************************************************ */
	private String getDelComp(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	DELETE FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_SEQ_NO=?	\n");
		return sql.toString();
	}	
	/** ***********************************************************************
	* �̺�Ʈ ���� ����
	************************************************************************ */
	private String getChgEvnt(TaoDataSet dataSet) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFEVNTAPLC SET	\n");
		
		sql.append("\t	EVNT_PGRS_CLSS = ?, NOTE=?, MGR_MEMO=?,   HADC_NUM=?,  CUS_RMRK=?	\n");
		sql.append("\t	, CNCL_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 		\n");
				
		sql.append("\t	, RSVT_DATE= ? 	\n");
		sql.append("\t	WHERE APLC_SEQ_NO=?	\n");
		return sql.toString();
	}	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getUpdateQuery(TaoDataSet dataSet) throws Exception{
		
		StringBuffer sql = new StringBuffer();
		sql.append("\n								");
		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT SET								");
		
		if(!"".equals(dataSet.getString("pu_date2"))) sql.append("\n	GREEN_NM = ?	 ,	");
		if(!"".equals(dataSet.getString("pgrs_yn"))) sql.append("\n		PGRS_YN = ?	 ,	");
		if(!"".equals(dataSet.getString("payType"))) sql.append("\n		CSLT_YN = ? ,	");
		if(!"".equals(dataSet.getString("memJoinType")))
		{
			sql.append("\n		LESN_SEQ_NO = ? ,	");
			sql.append("\n		STTL_AMT = ? ,		");
		}
		
		if(!"".equals(dataSet.getString("memo_expl"))) 
		{
			sql.append("\n		MEMO_EXPL = ? , 		");
		}
		
		//������ҽ� ó��
		if("C".equals(dataSet.getString("pgrs_yn"))) 
		{
			sql.append("\n		TEOF_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') , 		");
		}
		
		
		sql.append("\n		CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') , 		");
		sql.append("\n		CHNG_MGR_ID = ?  		");
		
		sql.append("\n	WHERE GOLF_SVC_APLC_CLSS = '1003' AND APLC_SEQ_NO = ?  				");	
		sql.append("\n								");
		return sql.toString();
	}

	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getImgDelQuery(String img_del_type) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBPMGDSMGMT SET								");
		if("IMG_NM".equals(img_del_type))
		{
			sql.append("\n	IMG_NM = ?													");
		}		
		sql.append("\n	WHERE SEQ_NO = ? 			");	
		return sql.toString();
	}
		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getDeleteQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBPMGDSMGMT	 							");
		sql.append("\n	WHERE SEQ_NO = ? 				");	
		return sql.toString();
	}	


}
