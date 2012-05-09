/***************************************************************************************************
 *   이 소스는 ㈜비씨카드 소유입니다.
 *   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 *   클래스명  : ISPCommon.java
 *   작성자    : (주)미디어포스 이경희
 *   내용      : ISP 공통 클래스
 *   적용범위  : Golf
 *   작성일자  : 2011.02.10
 ************************** 수정이력 ****************************************************************
*    일자       작성자      변경사항
 ***************************************************************************************************/
package com.bccard.golf.common.ispCert;

import java.util.HashMap;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.core.WaContext;

public class ISPCommon extends GolfActn{

	public void ispRecord(WaContext context, HashMap hmap) {				
		
		String ispAccessYn = hmap.get("ispAccessYn").toString();
		String veriResCode = hmap.get("veriResCode").toString();
		String title = hmap.get("title").toString();
		String memName = hmap.get("memName").toString();
		String memSocid = hmap.get("memSocid").toString();
		String ispCardNo = hmap.get("ispCardNo").toString();
		String cstIP = hmap.get("cstIP").toString();
		
		try {
			
            if(ispAccessYn.equals("Y")){
            	
                // ISP 로그 저장
                String logSaveResultYn = "N";

                ISPCertification ispProc = (ISPCertification)context.getProc("ISPCertification");

                DbTaoDataSet input = new DbTaoDataSet(title);
                input.setString("userNm",       memName);           //고객이름
                input.setString("userSocid",    memSocid);          //주민번호
                input.setString("userCardNo",   ispCardNo);         //ISP에서 넘어온 카드번호
                input.setString("userIp",       cstIP);             //클라이언트 IP
                input.setString("vfcRslt",      veriResCode);           //검증결과코드 (1: 정상주문완료   3:주문오류시)
                input.setString("urlType",      "BCGOLFN");         //사용메뉴명
                
                logSaveResultYn = ispProc.insertIspLog(context, input);                    

                if(logSaveResultYn.equals("Y")){
                	debug("## 적용 class name : " +hmap.get("className").toString() +
                			" | ISP 로그 저장 성공 | logSaveResultYn : "+logSaveResultYn+ " | userSocid : "+memSocid);
                }else {                	
                	debug("## " +hmap.get("className").toString() +
                			" | ISP 로그 저장 오류 발생 | logSaveResultYn : "+logSaveResultYn+ " | userSocid : "+memSocid);
                }
                
            }

        } catch(Exception e) {        	
        	debug("## " +hmap.get("className").toString() +" | ISP 로그 저장 오류 예외 발생"+e); 
        }
		
		
	}
}
