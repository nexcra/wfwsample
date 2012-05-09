package com.bccard.golf.dbtao.proc.mania;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GolfEgolfExpoInsDaoProc extends AbstractProc
{
  public static final String TITLE = "이데일리 골프엑스포 사전등록자 처리";

  public int execute(WaContext context, TaoDataSet data)
    throws DbTaoException, SQLException
  {
    Connection conn = null;
    PreparedStatement pstmt = null;
    int idx = 0;
    int result = 0;
    try
    {
      String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
      String userId = data.getString("usrId");
      String usrNm = data.getString("usrNm");
      String usrSex = data.getString("usrSex");
      String usrBirth = data.getString("usrBirth");
      String usrJob = data.getString("usrJob");
      String usr_ddd_no = data.getString("usr_ddd_no");
      String usr_tel_hno = data.getString("usr_tel_hno");
      String usr_tel_sno = data.getString("usr_tel_sno");
      String usr_hp_ddd_no = data.getString("usr_hp_ddd_no");
      String usr_hp_hno = data.getString("usr_hp_hno");
      String usr_hp_sno = data.getString("usr_hp_sno");
      String usr_email = data.getString("usr_email");
      String usr_zip = data.getString("usr_zip");
      String usr_addr = data.getString("usr_addr");
      String usr_dtl_addr = data.getString("usr_dtl_addr");
      String usr_cdhd_gn = data.getString("usr_cdhd_gn");

      conn = context.getDbConnection("default", null);
      conn.setAutoCommit(false);

      pstmt = conn.prepareStatement(getInsertQuery().toString());
      idx++; pstmt.setString(idx, golf_svc_aplc_clss);
      idx++; pstmt.setString(idx, userId);
      idx++; pstmt.setString(idx, usrSex);
      idx++; pstmt.setString(idx, usrNm);
      idx++; pstmt.setString(idx, usr_email);
      idx++; pstmt.setString(idx, usr_ddd_no);
      idx++; pstmt.setString(idx, usr_tel_hno);
      idx++; pstmt.setString(idx, usr_tel_sno);
      idx++; pstmt.setString(idx, usr_hp_ddd_no);
      idx++; pstmt.setString(idx, usr_hp_hno);
      idx++; pstmt.setString(idx, usr_hp_sno);
      idx++; pstmt.setString(idx, usr_zip);
      idx++; pstmt.setString(idx, usr_addr);
      idx++; pstmt.setString(idx, usr_dtl_addr);
      idx++; pstmt.setString(idx, usrBirth);
      idx++; pstmt.setString(idx, usrJob);
      idx++; pstmt.setString(idx, usr_cdhd_gn);
      result = pstmt.executeUpdate();
    }
    catch (Exception e) {
      try {
        conn.rollback();
      } catch (Exception localException1) {
      }
      MsgEtt msgEtt = new MsgEtt("ERROR", "이데일리 골프엑스포 사전등록자 처리", "시스템오류입니다.");
      throw new DbTaoException(msgEtt, e);
    } finally {
      conn.commit();
      conn.setAutoCommit(true);
      try { if (pstmt != null) pstmt.close();  } catch (Exception localException2) {
      }try {
        if (conn != null) conn.close(); 
      } catch (Exception localException3) {
      }
    }
    return 0;
  }

  private StringBuffer getInsertQuery() throws Exception
  {
    StringBuffer sb = new StringBuffer();

    sb.append("\tINSERT INTO BCDBA.TBGAPLCMGMT\t\n");
    sb.append("\t\t(APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, SEX_CLSS, CO_NM,\t\n");
    sb.append("\t\tEMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,\t\t\n");
    sb.append("\t\tZP, ADDR, DTL_ADDR, JUMIN_NO, BKG_PE_NM, CDHD_NON_CDHD_CLSS, REG_ATON)\t\n");
    sb.append("\tVALUES (\t\n");
    sb.append("\t\t(SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT), ?, 'R', ?, ?, ?, \n");
    sb.append("\t\t?, ?, ?, ?, ?, ?, ?,\t\n");
    sb.append("\t\t?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS') )\t\t\t\n");

    return sb;
  }
}