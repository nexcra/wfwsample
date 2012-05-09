/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : PointInfoResetJtProc
*   작성자    : (주)미디어포스 진현구
*   내용      : TOP 포인트 정보
*   적용범위  : Topn
*   작성일자  : 2009-03-31
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.info;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.DateFormat;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.TopPointEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/***************************************************************************************
 * Golf
 * @version unknown
 * @author  unknown
****************************************************************************************/
public class GolfPointInfoResetJtProc  extends AbstractProc
{
	//private final String POINT_TSN = "UJI003_SELECT";
	private final String BSNINPT   = "BSNINPT";					// 프레임웍 조회서비스 

	/** ********************************************************************************
	* TOP 포인트 정보 세션 리셋
	* @param 	context		WaContext 객체.
	* @param 	request		HttpServletRequest 객체.
	* @param 	juminNo		String 객체.
	* @param 	mbr			TopPointInfoEtt 객체. 
	* @return 	void
	********************************************************************************** */
	public void resetSession(WaContext context, HttpServletRequest request, String juminNo , TopPointInfoEtt mbr)
	{		
		JoltInput entity = null;
		TaoResult taoResult= null;
		ArrayList affiliates = new ArrayList();
		String updatedDate = null;		
		debug("==================resetSession  =====>");

		try {			
			entity = new JoltInput(BSNINPT);

			debug("==================resetSession  =====> try");
			entity.setServiceName(BSNINPT);
			entity.setString("fml_trcode", "MJF6010R0100");
			entity.setString("fml_arg1", "1");
			entity.setString("fml_arg2", "99000000" + juminNo + "   ");
			entity.setString("fml_arg3", "1");
			debug("==================resetSession  =====> juminNo" + juminNo);

			// ************************************************
			// Jolt Log 출력을 위한 처리 추가
			// Writted By, Lee Eun Ho, 2005-09-29
			// ************************************************			
			java.util.Properties prop = new java.util.Properties();
			prop.setProperty("RETURN_CODE","fml_ret1");
			JtProcess jt = new JtProcess();
			taoResult = jt.call(context, request, entity, prop);
			// ************************************************
			
			String resultCode = taoResult.getString("fml_ret1");			
			if(!("0000".equals(resultCode) || "0009".equals(resultCode) )){				
				throw new BaseException("ression error");			
			}
			
			int tpCount = 0;
			if(taoResult.containsKey("fml_ret16")) {					
				tpCount = Integer.parseInt(taoResult.getString("fml_ret16"));	// 항목수
			}
			
			if(tpCount > 0) {
				taoResult.first();	
				for(int i=0; i < tpCount; i++) {
					if(taoResult.containsKey("fml_ret18") && taoResult.containsKey("fml_ret19") && taoResult.containsKey("fml_ret20")) {
						String companyName = taoResult.getString("fml_ret18");		// 제휴업체명
						String pointType = taoResult.getString("fml_ret19");		// 포인트 구분코드
						String pointName = taoResult.getString("fml_ret20");		// 포인트 구분명

						int totalPoints = Integer.parseInt(taoResult.getString("fml_ret21"));				
						int topPoints = Integer.parseInt(taoResult.getString("fml_ret23"));				
						try {
							updatedDate = DateFormat.format(taoResult.getString("fml_ret22"), DateFormat.rawlongDate, DateFormat.longDotDate);
						} catch (Exception e) {
							updatedDate = "-";
						}
						TopPointEtt topPointEtt = new TopPointEtt(companyName, pointType, pointName, updatedDate, totalPoints, topPoints);
						boolean isTopPoint = "000000".equals(taoResult.getString("fml_ret17"));
						if (isTopPoint && TopPointEtt.TOPPOINT.equals(taoResult.getString("fml_ret19"))) {					
							mbr.setTopPoint(topPointEtt);					
						}
						else if (isTopPoint && TopPointEtt.TOPSK.equals(taoResult.getString("fml_ret19"))) {
							mbr.setSkPoint(topPointEtt);
						}
						else if (isTopPoint && TopPointEtt.SUPERPOINT.equals(taoResult.getString("fml_ret19"))) {
							mbr.setNPoint(topPointEtt);
						}
						else {
							affiliates.add(topPointEtt);
						}
					}
					taoResult.next();	
				} 
			} 
			
			if(mbr.getTopPoint() == null) mbr.setTopPoint(new TopPointEtt());
			if(mbr.getSkPoint() == null) mbr.setSkPoint(new TopPointEtt());
			mbr.setAffiliates(affiliates);						
		} catch (Throwable te) {
			//throw getErrorException("LOGIN_ERROR_0003",new String[]{"개인 정보 조회 실패"},te);     // Jolt 처리 에러
		}
	}
	
	/** ********************************************************************************
	* TOP 포인트 정보 세팅
	* @param 	context		WaContext 객체.
	* @param 	request		HttpServletRequest 객체.
	* @param 	juminNo		String 객체.
	* @return 	pointInfo	TopPointInfoEtt 객체
	********************************************************************************** */
	public TopPointInfoEtt getTopPointInfoEtt(WaContext context, HttpServletRequest request, String juminNo) throws Exception {
		debug("==================TopPointInfoEtt  =====>");

			
		ResultException re = null;
		JoltInput entity = null;
		TaoResult taoResult= null;
		ArrayList affiliates = new ArrayList();
		String updatedDate = null;
		TopPointInfoEtt pointInfo = new TopPointInfoEtt();

		try {

			entity = new JoltInput(BSNINPT);
			
			entity.setServiceName(BSNINPT);
			entity.setString("fml_trcode", "MJF6010R0100");
			entity.setString("fml_arg1", "1");
			entity.setString("fml_arg2", "99000000" + juminNo + "   ");
			entity.setString("fml_arg3", "1");


			// ************************************************ 
			// Jolt Log 출력을 위한 처리 추가
			// Writted By, Lee Eun Ho, 2005-09-29
			// ************************************************
		debug("========================MJF6010R0100 :" + entity.toString());
			java.util.Properties prop = new java.util.Properties();
			prop.setProperty("RETURN_CODE","fml_ret1");
			JtProcess jt = new JtProcess();
			taoResult = jt.call(context, request, entity, prop);
debug("	PointInfoResetJtProc taoResult :: " + taoResult);
			// ************************************************
			
			String resultCode = taoResult.getString("fml_ret1");			
			if(!("0000".equals(resultCode) || "0009".equals(resultCode) )){				
				throw new BaseException("ression error");			
			}
debug(">>>>>>>>>>>>>>>>> start");
			pointInfo.setName(GolfUtil.trim(taoResult.getString("fml_ret3")));
			pointInfo.setHomeTel(GolfUtil.trim(taoResult.getString("fml_ret4")));
			pointInfo.setHomeZip(GolfUtil.trim(taoResult.getString("fml_ret5")));
			pointInfo.setHomeAddr1(GolfUtil.trim(taoResult.getString("fml_ret6")));
			pointInfo.setHomeAddr2(GolfUtil.trim(taoResult.getString("fml_ret7")));
			pointInfo.setOfficeTel(GolfUtil.trim(taoResult.getString("fml_ret8")));
			pointInfo.setOfficeZip(GolfUtil.trim(taoResult.getString("fml_ret9")));
			pointInfo.setOfficeAddr1(GolfUtil.trim(taoResult.getString("fml_ret10")));
			pointInfo.setOfficeAddr2(GolfUtil.trim(taoResult.getString("fml_ret11")));
			pointInfo.setCompany(GolfUtil.trim(taoResult.getString("fml_ret12")));
			pointInfo.setDepartment(GolfUtil.trim(taoResult.getString("fml_ret13")));
			pointInfo.setConvertableSK(Integer.parseInt(GolfUtil.trim(taoResult.getString("fml_ret14"))));
			pointInfo.setConvertableMileage(Integer.parseInt(GolfUtil.trim(taoResult.getString("fml_ret15"))));
			pointInfo.setPcsNo(GolfUtil.trim(GolfUtil.trim(taoResult.getString("fml_ret24"))));
debug(">>>>>>>>>>>>>>>>> end");
			int tpCount = 0;
			if(taoResult.containsKey("fml_ret16")) {					
				tpCount = Integer.parseInt(taoResult.getString("fml_ret16"));	// 항목수
			}
			
			if(tpCount > 0) {
				taoResult.first();	
				for(int i=0; i < tpCount; i++) {
					if(taoResult.containsKey("fml_ret18") && taoResult.containsKey("fml_ret19") && taoResult.containsKey("fml_ret20")) {
						String companyCode = taoResult.getString("fml_ret17");		// 제휴업체코드
						String companyName = taoResult.getString("fml_ret18");		// 제휴업체명
						String pointType = taoResult.getString("fml_ret19");		// 포인트 구분코드
						String pointName = taoResult.getString("fml_ret20");		// 포인트 구분명
			
						int totalPoints = Integer.parseInt(taoResult.getString("fml_ret21"));				
						int topPoints = Integer.parseInt(taoResult.getString("fml_ret23"));	
		
						try {
							updatedDate = DateFormat.format(taoResult.getString("fml_ret22"), DateFormat.rawlongDate, DateFormat.longDotDate);
						} catch (Exception e) {
							updatedDate = "-";
						}
						TopPointEtt topPointEtt = new TopPointEtt(companyName, pointType, pointName, updatedDate, totalPoints, topPoints);
						topPointEtt.setCompanyCode(companyCode);	//제휴업체코드 추가
						
						boolean isTopPoint = "000000".equals(taoResult.getString("fml_ret17"));
						if (isTopPoint && TopPointEtt.TOPPOINT.equals(taoResult.getString("fml_ret19"))) {
							pointInfo.setTopPoint(topPointEtt);					
						}
						else if (isTopPoint && TopPointEtt.TOPSK.equals(taoResult.getString("fml_ret19"))) {					
							pointInfo.setSkPoint(topPointEtt);
						}
						else if (isTopPoint && TopPointEtt.SUPERPOINT.equals(taoResult.getString("fml_ret19"))) {
							pointInfo.setNPoint(topPointEtt);
						}
						else {
							affiliates.add(topPointEtt);
						}
					}
					taoResult.next();	
				} 			 
			}
			if(pointInfo.getTopPoint() == null) {pointInfo.setTopPoint(new TopPointEtt());
			//debug("========= pointInfo.getTopPoint()==="+pointInfo.getTopPoint());
			}
			if(pointInfo.getSkPoint() == null) pointInfo.setSkPoint(new TopPointEtt());
			pointInfo.setAffiliates(affiliates);						

		} catch(Throwable t) {
			
			throw new Exception();
		}

		return pointInfo;
	}
}
