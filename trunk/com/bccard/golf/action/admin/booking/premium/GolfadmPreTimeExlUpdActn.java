/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPreTimeExlUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾� ƼŸ�� ÷������ �ӽõ��
*   �������  : Golf
*   �ۼ�����  : 2010-06-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;

import com.bccard.golf.common.AppConfig;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.*;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.oreilly.servlet.MultipartRequest;

/******************************************************************************
* Golfloung
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmPreTimeExlUpdActn extends GolfActn{

	public static final	String TITLE ="÷������ �ӽõ��";

	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		PreparedStatement pstmt 		= null;
		PreparedStatement pstmtDate 	= null;	
		PreparedStatement pstmtTime 	= null;	
		PreparedStatement pstmtMaxDate 	= null;	
		PreparedStatement pstmtMaxTime 	= null;	
		PreparedStatement pstmtChkTime 	= null;	
		Connection con 					= null;
		ResultSet rs 					= null;
		ResultSet rsChkTime				= null;
		String subpage_key 				= "default";
		
		try {
			
			con = context.getDbConnection("default", null);
			con.setAutoCommit(false);
			
			// ������ ���丮�� ���� ��� ���� �����Ѵ�.
			String atcTmpPath = AppConfig.getAppProperty("UPLOAD_TMP_PATH")+"/bk_prm/tmp/";		// ������ ���丮
			atcTmpPath = atcTmpPath.replaceAll("\\.\\.","");	
			File createPath  =	new	File(atcTmpPath);
			if (!createPath.exists()){
				createPath.mkdirs();
			}
			int	intMaxSize	= 10*1024*1024; //2MB
			MultipartRequest multi  = new MultipartRequest(request, atcTmpPath, intMaxSize,"euc-kr", new com.oreilly.servlet.multipart.DefaultFileRenamePolicy());
			String orgNamePath = multi.getParameter("upFilePath");		// �����̸�
			String tmpFileName = multi.getFilesystemName  ("upFile");	// upload ������	���� �̸�
			String orgFileName = multi.getOriginalFileName("upFile");	// ���� ����	�̸�
	
			File file = new File(atcTmpPath,tmpFileName);	// ����
			long fsize = file.length();						// ���� ������

			String errMsg = "";		// �����޼���
			String failMerCode="";	// �����ڵ�
			int succ_cnt = 0;		// ���� ����
			int succ_day_cnt = 0;	// ��ŷ�� ���̺� �μ�Ʈ ���� �� 
			int fail_cnt = 0;		// ���� ����
			int worktotrow = 0;		// ���
			int workcol    = 0;		// ����
			
			String fname =  file.getName();					
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			worktotrow =  sheet.getRows();
	
			debug("## admPreTimeListActn | worktotrow : "+worktotrow+ " | fname : "+fname+" | ���� row"+sheet.getRows()+"\n");

			String gr_id = "";				// ������ ���̵�
			String gr_id_pre = "";			// ������ ���̵� ����
			int gr_seq = 0;					// ������ seq
			String bk_date = "";			// ��ŷ����
			int bk_date_seq = 0;			// ��ŷ���� seq
			int max_date_seq = 0;			// ��ŷ���� seq �ִ밪
			String bk_time = "";			// ��ŷ �ð�
			int int_bk_time = 0;			// ��ŷ �ð� seq
			int max_time_seq = 0;			// ��ŷ�ð� seq �ִ밪
			int bk_time_seq = 0;			// ��ŷ�ð� seq
			String gr_cs = "";				// �ڽ�
			String view_yn = "";			// ���⿩��
			int idx = 0;
			int result = 0;					// ó�� ���

			if ( worktotrow <= 0 ) {

				debug("��ȿ�� ������ �ƴմϴ�.eeeeeee");
				errMsg = "��ȿ�� �Ǽ��� �����ϴ�.";

			} else {

				// ��ŷ���� �ִ밪 �������� getMaxDateQuery
				pstmtMaxDate = con.prepareStatement(getMaxDateQuery());
				rs = pstmtMaxDate.executeQuery();
				if ( rs.next() ){
					max_date_seq = rs.getInt("BK_DATE_SEQ");
				}
				pstmtMaxDate.close();

				// ƼŸ�� idx �ִ밪 �������� getMaxTimeQuery
				pstmtMaxTime = con.prepareStatement(getMaxTimeQuery());
				rs = pstmtMaxTime.executeQuery();
				if ( rs.next() ){
					max_time_seq = rs.getInt("TIME_SEQ_NO")+succ_cnt;
				}
				pstmtMaxTime.close();
					

				String sqlTime = this.getTimeInsertQuery();
				pstmtTime = con.prepareStatement(sqlTime);
				
				// 1.�������� ��ȿ�� üũ 	
				for (int r = 1; r < worktotrow; r++) {
					
					try{
						workcol= 0;
						gr_id    	 = StrUtil.isNull(sheet.getCell(workcol, r).getContents(),"");
						bk_date    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						gr_cs    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						bk_time    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						view_yn    	 = StrUtil.isNull(sheet.getCell(++workcol, r).getContents(),"");  
						
						int_bk_time = Integer.parseInt(bk_time);
						//debug(" r : "+r+ " | gr_id : " + gr_id + " | bk_date : " + bk_date+ " | bk_time : " + bk_time+ " | gr_cs : " + gr_cs+ " | view_yn : " + view_yn);
						
						
						if (!"".equals(gr_id) && !"".equals(bk_date) && !"".equals(bk_time) && !"".equals(gr_cs) && !"".equals(view_yn)){
							
							if(!gr_id.equals(gr_id_pre) || gr_seq==0){
								// ������ idx ��������
								pstmt = con.prepareStatement(getGreenSeqSelectQuery());
								pstmt.setString(1, gr_id);
								rs = pstmt.executeQuery();
								if ( rs!=null && rs.next() ){
									gr_seq = rs.getInt("AFFI_GREEN_SEQ_NO");
								}
								pstmt.close();
							}
							
							if(gr_seq>0){

								// ��ŷ���� �˻� getChkDateQuery
								pstmt = con.prepareStatement(getChkDateQuery());
								idx = 0;
								pstmt.setInt(++idx, gr_seq);
								pstmt.setString(++idx, gr_cs);
								pstmt.setString(++idx, bk_date);
								rs = pstmt.executeQuery();
								if ( rs.next() ){
									bk_date_seq = rs.getInt("RSVT_ABLE_SCD_SEQ_NO");
								}else{
									// ��ŷ���� ����ϱ� getDateInsertQuery
									pstmtDate = con.prepareStatement(getDateInsertQuery());
									bk_date_seq = max_date_seq+succ_day_cnt;
									idx = 0;
									pstmtDate.setInt(++idx, bk_date_seq);
									pstmtDate.setInt(++idx, gr_seq);
									pstmtDate.setString(++idx, gr_cs);
									pstmtDate.setString(++idx, bk_date);
									pstmtDate.setString(++idx, gr_id);
//									pstmtDate.addBatch(); 
									result = pstmtDate.executeUpdate();
									succ_day_cnt++;
									pstmtDate.close();
								}
								pstmt.close();
								
								pstmtChkTime = null;
								rsChkTime = null;
								// ƼŸ�� ��ϵǾ� �ִ��� �˾ƺ��� getChkTimeQuery
								idx = 0;
								pstmtChkTime = con.prepareStatement(getChkTimeQuery());
								pstmtChkTime.setInt(++idx, bk_date_seq);
								pstmtChkTime.setInt(++idx, gr_seq);
								pstmtChkTime.setInt(++idx, int_bk_time);
								rsChkTime = pstmtChkTime.executeQuery();
								if ( rsChkTime.next() ){
									bk_time_seq = rsChkTime.getInt("RSVT_ABLE_BOKG_TIME_SEQ_NO");
									failMerCode = failMerCode+"<br> �ߺ������� : "+gr_id+" | "+bk_date+" | "+gr_cs+" | " + bk_time + " | bk_time_seq : " + bk_time_seq;
									fail_cnt++;
								}else{
									idx = 0;
									pstmtTime.setInt(++idx, max_time_seq+succ_cnt);
									pstmtTime.setInt(++idx, bk_date_seq);
									pstmtTime.setInt(++idx, gr_seq);
									pstmtTime.setString(++idx, bk_time);
									pstmtTime.setString(++idx, gr_id);
									pstmtTime.setString(++idx, view_yn);
									pstmtTime.addBatch();
									//result = pstmtTime.executeUpdate();
									succ_cnt++;
								}	
								pstmtChkTime.close();

								

							}else{
								failMerCode = failMerCode+"<br>�������ڵ� ���� : "+gr_id;
							}

							if (succ_cnt % 10000 == 0 ){
//								pstmtDate.executeBatch();
								pstmtTime.executeBatch();
							}
							
						}else{
							debug("������� ��ü�� ���� | gr_id : "+gr_id+" | bk_date : "+bk_date+" / bk_time : " + bk_time + "\n");
							if (errMsg.equals("") )
								errMsg = "������ȣ ���� ���Ͽ��� "+String.valueOf((r+1))+"��° �� ����Ÿ�� �̻��� �ֽ��ϴ�. �ٽ� �õ��Ͻñ� �ٶ��ϴ�.";
							fail_cnt++;
							
							if("".equals(failMerCode)) failMerCode = gr_id;
							else failMerCode = failMerCode+"<br>����������� : "+gr_id+" | "+bk_date+" | "+gr_cs+" | " + bk_time;

							fail_cnt++;
						}

						
						workcol++;
						gr_id_pre = gr_id;

					}catch(Throwable ignore){ 
					

						debug(TITLE, ignore);
						debug("���� ���Ͽ��� "+String.valueOf((r+1))+"��° ��, "+String.valueOf((workcol+1))+"���� Į������ ����Ÿ�� �̻��� �ֽ��ϴ�.\n���ڰ� �� �ִ��� Ȯ�� �� �ٽ� �õ��Ͻñ� �ٶ��ϴ�.");							
						fail_cnt++;
						if("".equals(gr_id)) failMerCode = gr_id;
						else failMerCode = failMerCode+"<br>�����Ϳ��� : "+gr_id+" | "+bk_date+" | "+gr_cs+" | " + bk_time;

					}
				}
				try{
					debug("## ��ġ����");
//					pstmtDate.executeBatch();
					pstmtTime.executeBatch();
					con.commit();
				}
				catch(Throwable ignore) 
				{
					succ_cnt=0;
					
				}
				con.setAutoCommit(true);
				
				System.out.print("## TpMerExcelUpLoadActn | succ_cnt : "+succ_cnt+ " | fail_cnt : "+fail_cnt+" | failMerCode : "+failMerCode+"\n");
			}
			
			worktotrow = worktotrow-1;
			request.setAttribute("succ_cnt" ,Integer.toString(succ_cnt));
			request.setAttribute("fail_cnt"	,Integer.toString(fail_cnt));
			request.setAttribute("total_cnt"	,Integer.toString(worktotrow));
			request.setAttribute("failMerCode"	,failMerCode);
			request.setAttribute("err_msg"	,errMsg);
			request.setAttribute("filename"	,fname);
			
			request.setAttribute("tmpFileName"	 ,tmpFileName);
			request.setAttribute("tmpFilePath"	 	,atcTmpPath);
			request.setAttribute("tmpFileSize"		 ,String.valueOf(fsize)	);
			request.setAttribute("orgFileName"	 ,orgFileName);
			request.setAttribute("orgNamePath"	 ,orgNamePath);
			request.setAttribute("varNm"			 ,multi.getParameter("varNm"));
			request.setAttribute("varSize"		 ,multi.getParameter("varSize"));
			request.setAttribute("varPoint"		 ,multi.getParameter("varPoint"));

	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	/** ***********************************************************************
	* ������ IDX ��������
	************************************************************************ */
	private String getGreenSeqSelectQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT AFFI_GREEN_SEQ_NO FROM BCDBA.TBGAFFIGREEN WHERE GREEN_ID=?	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ��ŷ���� �˻�
	************************************************************************ */
	private String getChkDateQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT RSVT_ABLE_SCD_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT WHERE AFFI_GREEN_SEQ_NO=? AND GOLF_RSVT_CURS_NM=? AND BOKG_ABLE_DATE=? 	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ��ŷ���� �ִ밪 ��������
	************************************************************************ */
	private String getMaxDateQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT MAX(NVL(RSVT_ABLE_SCD_SEQ_NO,0))+1 BK_DATE_SEQ FROM BCDBA.TBGRSVTABLESCDMGMT 	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ��ŷ���� ����ϱ�
	************************************************************************ */
	private String getDateInsertQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGRSVTABLESCDMGMT (	");
		sql.append("\n	RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, GOLF_RSVT_CURS_NM, BOKG_ABLE_DATE, REG_MGR_ID, REG_ATON, GOLF_RSVT_DAY_CLSS	");
		sql.append("\n	) VALUES (	");
		sql.append("\n			?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD'),'P'	");
		sql.append("\n	)	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ��ŷ�ð� �˻�
	************************************************************************ */
	private String getChkTimeQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT WHERE RSVT_ABLE_SCD_SEQ_NO=? AND AFFI_GREEN_SEQ_NO=? AND TO_NUMBER(BOKG_ABLE_TIME)=?	"); 
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ��ŷ�ð� �ִ밪 ��������
	************************************************************************ */
	private String getMaxTimeQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT NVL(MAX(RSVT_ABLE_BOKG_TIME_SEQ_NO),0)+1 TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ��ŷ�ð� ����ϱ�
	************************************************************************ */
	private String getTimeInsertQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	");
		sql.append("\n			RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, BOKG_ABLE_TIME, REG_MGR_ID, REG_ATON, EPS_YN ,BOKG_RSVT_STAT_CLSS	");
		sql.append("\n	) VALUES (	");
		sql.append("\n			?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, '0001'	");
		sql.append("\n	)	");
		return sql.toString();
	}
	
}
