/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemMonthInsDaoProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ��ȸ�� ó�� ���μ��� 
*   			GolfLoginInsProc.java���� ȣ�� (�� Ŭ������ ���ϱ� ������ ���� ����) 
*     			if(����ȸ��){
*     				if(Żȸȸ��){ 
*     					//�簡�� ȸ�� ó��
*     				}else { //Żȸȸ�� �ƴ� ���� ����ִ� ȸ�� ó��
*						- ��Ÿ ���μ���
*						- ��ȸ�� ȸ�� ó�� (GolfMemMonthInsDaoProc.java)   
*     				}
*    			else{ 
*    				//����ȸ�� �ƴ�, ��, ���� �ű�ȸ�� ó�� 
*    			}
*     
*   �������  : golf 
*   �ۼ�����  : 20110620
************************** �����̷� ****************************************************************
*    ����     �ۼ���   �������
*2011.12.28  �̰���	  �� Ŭ������ ���� ��ȸ��(����Ʈ�ø���)�� ��ȸ�� ����Ʈ�ø�� �߰�
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.tao.TaoException;

public class GolfMemMonthInsDaoProc extends AbstractProc {
	
	public static final String TITLE = "����Ʈ��� ó�� Proc";	
	
	
	/**
 	 *	<pre>
	 * 	<li> 
 	 * 	</pre>	  
	 *	@param
	 *  @param
	 *  @return    
	 * @throws TaoException 
	 */	
	public int execute_MonthMember(Connection con, String socId, String cdhdId) throws TaoException  {
		
		String title				= "����Ʈ���  ȸ������ �˾ƺ���.";
		int whatGrade				= 0;		// ���
		int whatJoinChnl			= 0;		// ���԰��
		int grd						= 0;		// ���۵����Ϳ��� ������ ���
		int vals[]					= new int[3];
		int successCnt				= 0;
		String end_date 			= "";		// ����Ⱓ ������
		boolean chk 				= false;
		
		DbTaoResult result = isMonthMember(con, socId, cdhdId);
		
        while( result.isNext() ){
        	
        	result.next();   		
		
        	vals =  (int[]) result.getObject("retVals"); 
			whatJoinChnl = vals[0];
			whatGrade = vals[1];
			grd	= vals[2];
			
			if (chk) whatGrade = 0;
			
			//grd != 0  ������ ���� ������ ����
			if ( grd != 0 ){
			
				if(whatGrade==0){ //��� ���̺� �������� �ʴ�  TM ���� ��ȸ�� ȸ��
					
					successCnt = execute_inGrd(con, socId, cdhdId, grd, whatJoinChnl);
					
				}else if(whatGrade==8){ // white ȸ���̸�  ������� ���׷��̵�
					
					chk = true;
					successCnt = execute_upgrade(con, socId, cdhdId, grd, whatJoinChnl);
					
				}else { // ���� ����� �̹� ����; ���� �ø� ? ; TM ���� ����Ʈ��� ȸ�� // ������Ʈ�� ä���� �ٸ���? ä�ε�������Ʈ?
					
					successCnt = execute_updExistGrd(con, socId, cdhdId, grd, whatJoinChnl);
					
				}
				
			}
			
        }
	
		return successCnt;	
	}
	
	/**
	 *	<pre>
	 * 	<li> ��ȸ�� ��� ������� Ȯ�� 
	 * 	</pre>
	 *  @return String returnGrd
	 */
	private DbTaoResult isMonthMember(Connection con, String socId, String cdhdId) throws DbTaoException  {
		
		String title				= "����Ʈ��� Ȯ�� : isMonthMember()";
		
		String sql 					= "";
		int grd						= 0; //���		
		int returnGrd				= 0; //������
				
		ResultSet rs 				= null;		
		PreparedStatement pstmt		= null;
		
		DbTaoResult result =  new DbTaoResult(title);
		DbTaoResult result2 =  new DbTaoResult(title);
		
		try {
			
			//����ȸ���ε����̺��� ���԰�ο� ����� �����´�.
			sql = getTMOfferInfo();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			
			if(rs != null ){ 
				while (rs.next()){				
				
					result.addInt("joinChnl", rs.getInt("RCRU_PL_CLSS"));
					result.addInt("grd",	rs.getInt("GRADE"));
					
				}
			}
			
			sql = null;
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
                        
            while( result.isNext() ){
            	
            	result.next();           
            
	            //����ȸ����ް������̺� ���� ����� �����ϴ���
				sql = getMonthGrade();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, cdhdId);
				pstmt.setInt(2, result.getInt("grd"));
				rs = pstmt.executeQuery();			
					
				if(rs.next()){			
					returnGrd = rs.getInt("CDHD_CTGO_SEQ_NO");				
				}else {
					returnGrd = 0; // ���� ��� ���� �ǹ�				
				}
				
				sql = null;
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	
	            //���� ��� ������ ȭ��Ʈ ����� �����ϴ���
	            if (returnGrd == 0){		
	
					sql = getMonthGrade();			
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, cdhdId);	
					pstmt.setString(2, "8");
					rs = pstmt.executeQuery();
						
					if(rs.next()){
						returnGrd = rs.getInt("CDHD_CTGO_SEQ_NO");							
					}
					
	            }			
	            
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            grd = result.getInt("grd");	       
	            
	            int retVals[] = new int[3];
	            retVals[0] = result.getInt("joinChnl");
	    		retVals[1] = returnGrd;
	    		retVals[2] = grd;
	    			    		
	    		result2.addObject("retVals", retVals);
	    		
            }
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result2;
		
	}

	
	/**
	 *	<pre>
	 * 	<li> �����̳�, Żȸȸ�� �簡�Խ� TM ���� ����Ʈ��� ȸ�� ������� �˾ƺ���
	 * 	</pre> execute_MonthMember
	 */
	public boolean execute_newJoinMemYN(Connection con, String socId) throws TaoException {

		String title				= "�����̳�, Żȸȸ�� �簡�Խ� TM ���� ����Ʈ��� ȸ�� ������� �˾ƺ���";
		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;

		boolean flag 				= false;		
				
		try {
			
			//����ȸ���ε����̺��� ���԰�ο� ����� �����´�.
			pstmt = con.prepareStatement(getTMOfferInfo());			
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			
			while(rs.next()){ 
				flag = true;
			}

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return flag;
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li> ��� ���̺� �������� �ʴ� TM ���� ����Ʈ��� ȸ�� ��� ó��
	 * 	</pre>
	 */
	private int execute_inGrd(Connection con, String socId, String cdhdId, int grade, int joinChnl) throws TaoException {

		String title				= "��� ���̺� �������� �ʴ�  TM ����; ����Ʈ��� ȸ�� ��� ó��";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0;
		
		try {
			
			// ��ϵǾ� ���� �ʴٸ�  �μ�Ʈ ���ش�.
			/**SEQ_NO ��������**************************************************************/
            pstmt = con.prepareStatement(getNextValQuery());
            rs = pstmt.executeQuery();			
			long max_seq_no = 0L;
			if(rs.next()){
				max_seq_no = rs.getLong("SEQ_NO");
			}
			
            /**Insert************************************************************************/
			pstmt = con.prepareStatement(getInsertGradeQuery());			
        	pstmt.setLong(1, max_seq_no ); 
        	pstmt.setString(2, cdhdId ); 
        	pstmt.setInt(3, grade );
			pstmt.executeUpdate();
        	
            //��ǥ ��� ����
            topGradeChange(con, cdhdId, grade, joinChnl, "1");
	        
			// ���� �� ����ȸ��  ���̺� ������Ʈ
			idx = 0;
			pstmt = con.prepareStatement(exeUpdOfferEnd());
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, joinChnl );
        	pstmt.setString(++idx, socId );
        	result = pstmt.executeUpdate();
        	if(pstmt != null) pstmt.close();
	        
	        info(" || cdhdId : "+ cdhdId + " | ��� ���̺� �������� �ʴ�  TM ����; ����Ʈ��� ȸ�� ��� ó�� �Ϸ� ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> White -> ����Ʈ��� ������� ������Ʈ  // ī�� ����ε� �Ⱓ�� �־�� �ϴ��� �ٽ� Ȯ��-�ȳ����� ����
	 * 	</pre>
	 *  @return int ��� cnt
	 */
	private int execute_upgrade(Connection con, String socId, String cdhdId, int grade, int joinChnl) throws TaoException {

		String title				= "White -> ����Ʈ������� ������Ʈ";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0;
		
		try {
			
			//������ �� ��� �����丮�� ���
			idx = 0;
			pstmt = con.prepareStatement(inGrdHistoryQuery());			
        	pstmt.setString(++idx, cdhdId );
        	pstmt.executeUpdate();			
            if(pstmt != null) pstmt.close();        	

    		// ��ȸ�� ��� - ���  ���̺� ���׷��̵�				
			idx = 0;
			pstmt = con.prepareStatement(exeUpdGrd());
        	pstmt.setInt(++idx, grade );
        	pstmt.setString(++idx, cdhdId );
        	pstmt.executeUpdate();	
            if(pstmt != null) pstmt.close();
        	
            //��ǥ ��� ����
            topGradeChange(con, cdhdId, grade, joinChnl, "2");
	        
			// ���� �� ����ȸ��  ���̺� ������Ʈ
			idx = 0;
			pstmt = con.prepareStatement(exeUpdOfferEnd());
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, joinChnl );
        	pstmt.setString(++idx, socId );
        	result = pstmt.executeUpdate();
        	if(pstmt != null) pstmt.close();
	        
	        info(" || cdhdId : "+ cdhdId + " | White -> ����Ʈ��� ���� ������Ʈ �Ϸ� ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li> ����Ʈ����� ����� ���ο� ������ �����Ƿ� �Ⱓ ������Ʈ 
	 * 	</pre>
	 */	
	private int execute_updExistGrd(Connection con, String socId, String cdhdId, int grade, int joinChnl) throws TaoException {

		String title				= "����Ʈ����� ����� ���ο� ������ �����Ƿ� �Ⱓ ������Ʈ";		
		PreparedStatement pstmt		= null;
		
		int idx = 0;
		int result = 0;		
		
		try {
			
        	//TBGGOLFCDHDGRDMGMT(����ȸ����ް���) ������Ʈ
			idx = 0;			
			pstmt = con.prepareStatement(exeUpdExistGrd());				
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, grade );
        	pstmt.executeUpdate();        	
        	if(pstmt != null) pstmt.close();
        	
			// ���� �� ����ȸ��  ���̺� ������Ʈ
			idx = 0;
			pstmt = con.prepareStatement(exeUpdOfferEnd());
        	pstmt.setString(++idx, cdhdId );
        	pstmt.setInt(++idx, joinChnl );
        	pstmt.setString(++idx, socId );
        	result = pstmt.executeUpdate();
        	if(pstmt != null) pstmt.close();
        	
        	info(" || cdhdId : "+ cdhdId + " | ����Ʈ����� ����� ���ο� ������ �����Ƿ� �Ⱓ ������Ʈ  �Ϸ� ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}			


	/**
	 *	<pre>
	 * 	<li> ��ǥ ��� ������Ʈ
	 * 	</pre>
	 * @throws DbTaoException 
	 */
	private void topGradeChange(Connection con, String cdhdId, int grade, int joinChnl, String gubun) throws DbTaoException {
	
		String title				= "��ǥ ��� ������Ʈ";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		PreparedStatement pstmt2		= null;
		
		int idx = 0;
				
		try {
        	
        	// ���� �ڱ��޺��� ���� ����� �ִ��� �˾ƺ���.
            idx = 0;
			pstmt = con.prepareStatement(getGrdChgYN());
			pstmt.setInt(++idx, grade);	
			pstmt.setString(++idx, cdhdId);	
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				
				if("Y".equals(rs.getString("CHG_YN"))){
					idx = 0;
					//����Ʈ��� - ����ȸ�� ���̺� ������Ʈ
					pstmt2 = con.prepareStatement(exeUpdTopGrade(gubun));
					pstmt2.setInt(++idx, grade);	
					if (gubun.equals("2")){
						pstmt2.setInt(++idx, joinChnl );
					}
					pstmt2.setString(++idx, cdhdId);	
					pstmt2.executeUpdate();					
				}					
				//N �϶�, ����ü���� ��� �ϳ�?
			}
	    	
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}            
	        try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
		}
		
	}


	/**
	 *	<pre>
	 * 	<li> ����ȸ���ε����̺��� ���� ������ ȸ���� ���԰�ο� ����� �����´�.
	 * 	<li> ����Ʈ ��� {(Smart150, 200, 500, ķ���αⰣ�� 3����), (NH����Ʈ5000  CAMP_END_DATE��¥�� ����ȸ�� ����Ⱓ ����)}
	 * 	</pre>
	 *  @return String ����
	 */		
	private String getTMOfferInfo(){
		 
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
  		sql.append("\t	SELECT OFFER.RCRU_PL_CLSS, OFFER.MEMO_EXPL GRADE	\n");
  		sql.append("\t	FROM BCDBA.TBACRGCDHDLODNTBL OFFER	\n");
  		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON OFFER.JUMIN_NO = TB_INFO.SOCID	\n");
  		sql.append("\t	WHERE OFFER.SITE_CLSS='02' AND PROC_RSLT_CLSS<>'01' \n");
  		sql.append("\t	AND MEMO_EXPL IN ( \n");
  		sql.append("\t						SELECT GOLF_CMMN_CODE	 	\n");
  		sql.append("\t	 					FROM BCDBA.TBGCMMNCODE		\n");
  		sql.append("\t	 					WHERE GOLF_CMMN_CLSS='0064'	\n");
  		sql.append("\t	 					AND GOLF_CMMN_CODE != '0027'	\n");
  		sql.append("\t	) \n");  		
  		sql.append("\t	AND OFFER.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  OFFER.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
  		sql.append("\t	AND OFFER.JUMIN_NO = ?	\n");
		
		return sql.toString();
	
	}
     
     
 	/**
  	 *	<pre>
 	 * 	<li> ����ȸ����ް������̺� ���� ����� �����ϴ���
  	 * 	</pre>
 	 *  @return String ����
 	 */
	private String getMonthGrade(){
		
		StringBuffer sql = new StringBuffer();

		sql.append("	\n");
		sql.append("\t	SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD		\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTG ON GRD.CDHD_CTGO_SEQ_NO=CTG.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE CDHD_ID = ? AND CTG.CDHD_CTGO_SEQ_NO = ?	\n");
		
		return sql.toString();
	
	}
	
	
 	/**
  	 *	<pre>
 	 * 	<li> ��� �����丮 ���̺� �μ�Ʈ    
  	 * 	</pre>
 	 *  @return String ����
 	 */	
 	private String inGrdHistoryQuery(){
 		
 		StringBuffer sql = new StringBuffer();
 		
 		sql.append("	\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
 		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID	\n");
 		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002'	\n");
 		
 		return sql.toString(); 		
 		
 	}

     
    /**
   	 *	<pre>
  	 * 	<li> ��޺��� REG_ATON �� �־�� ���� �ʳ�?
   	 * 	</pre>
  	 *  @return String ����
  	 */     
  	private String exeUpdGrd(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
  		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
  		sql.append("\t  , REG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT = NULL	 \n"); 
  		sql.append("\t  WHERE CDHD_GRD_SEQ_NO=(	\n");
  		sql.append("\t      SELECT GRD.CDHD_GRD_SEQ_NO	\n");
  		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
  		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t      WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002' AND GRDM.CDHD_CTGO_SEQ_NO = '8'	\n");
  		sql.append("\t  )	\n");

  		return sql.toString();
  	}   

  	
    /**
   	 *	<pre>
  	 * 	<li>  ��ǥ��� ���濩�� ���
   	 * 	</pre>
  	 *  @return String ����
  	 */   	
  	private String getGrdChgYN(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
		sql.append("\t  SELECT (CASE WHEN T_CTGO.SORT_SEQ>(SELECT SORT_SEQ FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=?) THEN 'Y' ELSE 'N' END) CHG_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		
  		return sql.toString();
  		
  	}
  	

    /**
   	 *	<pre>
  	 * 	<li>  ��ǥ��� ������Ʈ
   	 * 	</pre>
  	 *  @return String ����
  	 */     	
	private String exeUpdTopGrade(String gubun){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO = ?	\n");	
		if (gubun.equals("2")){
			sql.append("\t  , JOIN_CHNL = ?	\n");
		}
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}  	
     
	/**
	 *	<pre>
	 * 	<li>  ����Ʈ��� �Ϸ� �� ������Ʈ
	 * 	</pre>
	 *  @return String ����
	 */	 
	private String exeUpdOfferEnd(){
		 
		StringBuffer sql = new StringBuffer();
			 
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS=? AND JUMIN_NO=?	\n");		
		
		return sql.toString();
	
	}	
     

 	/**
 	 *	<pre>
 	 * 	<li> ����Ʈ��� ���� ��� �̹� ����� ������Ʈ (�Ⱓ)  
 	 * 	</pre>
 	 *  @return String ����
 	 */	 	     
  	private String exeUpdExistGrd(){
  		
  		//��������� ���� �ϹǷ� �����Ͻø� ����
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
  		sql.append("\t  SET CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
  		sql.append("\t  , REG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");  
  		sql.append("\t  , CHNG_RSON_CTNT = NULL								\n");   
  		sql.append("\t  WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=?				\n"); 		 		
  		

  		return sql.toString();
  		
  	} 
  	
  	
 	/**
 	 *	<pre>
 	 * 	<li> Max IDX Query�� �����Ͽ� �����Ѵ�. = ����ȸ����ް���
 	 * 	</pre>
 	 *  @return String ����
 	 */
  	private String getNextValQuery(){
	
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n");
		sql.append("SELECT NVL(MAX(CDHD_GRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
		
		return sql.toString();
		
	}

  	
 	/**
 	 *	<pre>
 	 * 	<li> ����ȸ����ް��� �μ�Ʈ - TBGGOLFCDHDGRDMGMT 
 	 * 	</pre>
 	 *  @return String ����
 	 */  	
     private String getInsertGradeQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (							\n");
 		sql.append("\t  		CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
 		sql.append("\t  		) VALUES (												\n");
 		sql.append("\t  		?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')			\n");
 		sql.append("\t  		)														\n");
         return sql.toString();
     }  	

}
