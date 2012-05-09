/**********************************************************************************************************************
*   클래스명  : EtaxSessionEtt
*   작성자    : csj007
*   내용      : 사용자 Session Attribute Entity
*   적용범위  :etax
*   작성일자  : 2008.07.01
************************** 수정이력 ***********************************************************************************
*    일자      버전   작성자   변경사항
*
**********************************************************************************************************************/
package com.bccard.golf.common;

import com.bccard.waf.core.AbstractEntity;

/** ****************************************************************************
 * User Session Attribute Entity
 * @version   2008.07.01
 * @author    <A href="mailto:thisline@e4net.net">yiseon baek</A>
 **************************************************************************** */


public class GolfSessionEtt extends AbstractEntity {
    private String memId;            //아이디
	private String memNm;            //이름
	private String memClss;          //사용자구분(1=개인,2=법인,3=관리자)
	private String memNo;		     //관리자 PK번호
	private boolean  login = false;  //로그인여부
	private String strDocRoot    ;    /* DocRoot    업로드    */
	private String strWebRoot    ;    /* WebRoot    업로드     */
	private String  topMenu;  
	private String  leftMenu; 
	private String  golfCardCo;   
	
 
/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @return  public 
********************************************************************************** */ 
    public GolfSessionEtt(){
    	
    	this.strDocRoot      =  "" ;
    	this.strWebRoot      =  "" ;
    }
    
    /** ******************************************************************************** 
    * 기업
    * @version 2008.10.16 
    * @author csj007
    * @param self_no String객체.
    * @return  void 
    ********************************************************************************** */ 
    public void setGolfCardCoYn(String golfCardCo)                { this.golfCardCo = golfCardCo; }
    /** ******************************************************************************** 
    * 기업
    * @version 2008.10.16 
    * @author csj007
    * @param self_no String객체.
    * @return  void 
    ********************************************************************************** */ 
    public String getGolfCardCoYn()    { return this.golfCardCo;}
/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @param self_no String객체.
* @return  void 
********************************************************************************** */ 
    public void setMemId(String memId)                { this.memId = memId; }

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @param self_no String객체.
* @return  void 
********************************************************************************** */ 
    public void setMemNo(String memNo)                { this.memNo = memNo; }    

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @param self_no String객체.
* @return  void 
********************************************************************************** */ 
    public void setMemNm(String memNm)                { this.memNm = memNm; }

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @param self_no String객체.
* @return  void 
********************************************************************************** */ 
    public void setMemClss(String memClss)                { this.memClss = memClss; }

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @param self_no String객체.
* @return  void 
********************************************************************************** */ 
    public void setLogin(boolean login)                { this.login = login; }



/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemId()    { return this.memId;}
    
/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemNo()    { return this.memNo;}   

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemNm()    { return this.memNm;}

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemClss()    { return this.memClss;}

/** ******************************************************************************** 
* 기업
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public boolean isLogin()    { return this.login;}

    /**
	 * @info DocRoot 가맹점
	 * @param String param
	 * @return void
	 */
	public void setDocRoot(String param)    { this.strDocRoot    = param; }
    /**
	 * @info DocRoot 가맹점
	 * @return String
	 */
	public String getDocRoot()    { return strDocRoot   ; }
	 /**
	 * @info DocRoot 가맹점
	 * @param String param
	 * @return void
	 */
	public void setWebRoot(String param)    { this.strWebRoot    = param; }
    /**
	 * @info DocRoot 가맹점
	 * @return String
	 */
	public String getWebRoot()    { return strWebRoot   ; }
    
	 /**
	 * @info DocRoot 가맹점
	 * @return String
	 */
	public String getTopMenu()    { return topMenu   ; }	
	/**
	 * @info DocRoot 가맹점
	 * @return String
	 */
	public String getLeftMenu()    { return leftMenu   ; }	
}
