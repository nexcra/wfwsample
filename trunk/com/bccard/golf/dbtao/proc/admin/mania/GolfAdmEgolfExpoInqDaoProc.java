
package com.bccard.golf.dbtao.proc.admin.mania;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;

public class GolfAdmEgolfExpoInqDaoProc extends AbstractProc
{

    public GolfAdmEgolfExpoInqDaoProc()
    {
    }

    public DbTaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet data)
        throws BaseException
    {
        ResultSet rs;
        Connection conn;
        PreparedStatement pstmt;
        DbTaoResult result;
        int idx;
        String title = data.getString("TITLE");
        rs = null;
        conn = null;
        pstmt = null;
        result = new DbTaoResult(title);
        idx = 0;
        try
        {
            String usrNm = data.getString("USRNM");
            String pgrs_yn = data.getString("PGRS_YN");
            long page_no = data.getLong("PAGE_NO");
            long record_no = data.getLong("RECORD_SIZE");
            String aplc_clss = data.getString("APLC_CLSS");
            conn = context.getDbConnection("default", null);
            pstmt = conn.prepareStatement(getEgolfExpoListQuery(data).toString());
            pstmt.setLong(++idx, record_no);
            pstmt.setString(++idx, aplc_clss);
            if(!GolfUtil.isNull(usrNm))
                pstmt.setString(++idx, usrNm);
            if(!GolfUtil.isNull(pgrs_yn))
                pstmt.setString(++idx, pgrs_yn);
            pstmt.setLong(++idx, page_no);
            rs = pstmt.executeQuery();
            if(rs != null)
                for(; rs.next(); GolfUtil.toTaoResult(result, rs));
            if(result.size() < 1)
                result.addString("RESULT", "01");
            else
                result.addString("RESULT", "00");
        }
        catch(Throwable t)
        {
            throw new BaseException(t);
        }

        try
        {
            if(rs != null)
                rs.close();
        }
        catch(Exception exception1) { }
        try
        {
            if(pstmt != null)
                pstmt.close();
        }
        catch(Exception exception2) { }
        try
        {
            if(conn != null)
                conn.close();
        }
        catch(Exception exception3) { }
        
        try
        {
            if(rs != null)
                rs.close();
        }
        catch(Exception exception4) { }
        try
        {
            if(pstmt != null)
                pstmt.close();
        }
        catch(Exception exception5) { }
        try
        {
            if(conn != null)
                conn.close();
        }
        catch(Exception exception6) { }
        return result;
    }

    public DbTaoResult detail_execute(WaContext context, HttpServletRequest request, TaoDataSet data)
        throws BaseException
    {
        ResultSet rs;
        Connection conn;
        PreparedStatement pstmt;
        DbTaoResult result;
        String title = data.getString("TITLE");
        rs = null;
        conn = null;
        pstmt = null;
        result = new DbTaoResult(title);
        try
        {
            String aplc_clss = data.getString("APLC_CLSS");
            String aplc_seq_no = data.getString("APLC_SEQ_NO");
            conn = context.getDbConnection("default", null);
            pstmt = conn.prepareStatement(getEgolfExpoInqQuery(data).toString());
            pstmt.setString(1, aplc_clss);
            pstmt.setInt(2, Integer.parseInt(aplc_seq_no));
            rs = pstmt.executeQuery();
            if(rs != null && rs.next())
                GolfUtil.toTaoResult(result, rs);
        }
        catch(Throwable t)
        {
            throw new BaseException(t);
        }

        try
        {
            if(rs != null)
                rs.close();
        }
        catch(Exception exception1) { }
        try
        {
            if(pstmt != null)
                pstmt.close();
        }
        catch(Exception exception2) { }
        try
        {
            if(conn != null)
                conn.close();
        }
        catch(Exception exception3) { }
        
        try
        {
            if(rs != null)
                rs.close();
        }
        catch(Exception exception4) { }
        try
        {
            if(pstmt != null)
                pstmt.close();
        }
        catch(Exception exception5) { }
        try
        {
            if(conn != null)
                conn.close();
        }
        catch(Exception exception6) { }
        return result;
    }

    private StringBuffer getEgolfExpoListQuery(TaoDataSet data)
        throws Exception
    {
        String co_nm = data.getString("USRNM");
        String pgrs_yn = data.getString("PGRS_YN");
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n");
        sb.append("\t\tRNUM, APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, SEX_CLSS, CO_NM,\t\t\t\t\t\n");
        sb.append("\t\tEMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,\t\t\t\t\t\n");
        sb.append("\t\tZP, ADDR, DTL_ADDR, REG_ATON, JUMIN_NO, CDHD_NON_CDHD_CLSS, PAGE CURR_PAGE, TOT_CNT TOTAL_CNT\t\n");
        sb.append("\tFROM (\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n");
        sb.append("     SELECT                                                                              \n");
        sb.append("         ROWNUM RNUM, APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, SEX_CLSS, CO_NM,         \n");
        sb.append("         EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,             \n");
        sb.append("         ZP, ADDR, DTL_ADDR, REG_ATON, JUMIN_NO, CDHD_NON_CDHD_CLSS,                     \n");
        sb.append("         CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT                               \t\n");
        sb.append("     FROM                                                                                \n");
        sb.append("         (SELECT                                                                         \n");
        sb.append("             ROWNUM AS RNUM, APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, SEX_CLSS, CO_NM,  \n");
        sb.append("             EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,         \n");
        sb.append("             ZP, ADDR, DTL_ADDR, NVL(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'),'') REG_ATON, JUMIN_NO, CDHD_NON_CDHD_CLSS                  \n");
        sb.append("         FROM                                                                            \n");
        sb.append("             BCDBA.TBGAPLCMGMT                                                           \n");
        sb.append("         WHERE                                                                           \n");
        sb.append("             GOLF_SVC_APLC_CLSS = ?                                                 \t\t\n");
        sb.append("         ORDER BY APLC_SEQ_NO DESC)                                                      \n");
        sb.append("     WHERE 1=1                                                                           \n");
        if(!GolfUtil.isNull(co_nm))
            sb.append("         AND CO_NM LIKE ?                                                            \n");
        if(!GolfUtil.isNull(pgrs_yn))
            sb.append("         AND PGRS_YN = ?                                                             \n");
        sb.append(" ) WHERE PAGE = ?                                                                        \n");
        return sb;
    }

    private StringBuffer getEgolfExpoInqQuery(TaoDataSet data)
        throws Exception
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\tSELECT                                                                         \t\n");
        sb.append(" \tAPLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, SEX_CLSS, CO_NM,  \t\t\t\t\n");
        sb.append("     EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,         \n");
        sb.append("     SUBSTR(ZP, 0,3) ||'-'|| SUBSTR(ZP, 4,3) ZP, NVL(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'),'') REG_ATON,\t\n");
        sb.append("     ADDR, DTL_ADDR, JUMIN_NO, BKG_PE_NM, CDHD_NON_CDHD_CLSS       \t\n");
        sb.append(" FROM                                                                            \n");
        sb.append("     BCDBA.TBGAPLCMGMT                                                           \n");
        sb.append(" WHERE                                                                           \n");
        sb.append("     GOLF_SVC_APLC_CLSS = ?                                                 \t\t\n");
        sb.append("     AND APLC_SEQ_NO = ?                                                 \t\t\n");
        return sb;
    }
}