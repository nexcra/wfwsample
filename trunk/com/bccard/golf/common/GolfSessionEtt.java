/**********************************************************************************************************************
*   Ŭ������  : EtaxSessionEtt
*   �ۼ���    : csj007
*   ����      : ����� Session Attribute Entity
*   �������  :etax
*   �ۼ�����  : 2008.07.01
************************** �����̷� ***********************************************************************************
*    ����      ����   �ۼ���   �������
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
    private String memId;            //���̵�
	private String memNm;            //�̸�
	private String memClss;          //����ڱ���(1=����,2=����,3=������)
	private String memNo;		     //������ PK��ȣ
	private boolean  login = false;  //�α��ο���
	private String strDocRoot    ;    /* DocRoot    ���ε�    */
	private String strWebRoot    ;    /* WebRoot    ���ε�     */
	private String  topMenu;  
	private String  leftMenu; 
	private String  golfCardCo;   
	
 
/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @return  public 
********************************************************************************** */ 
    public GolfSessionEtt(){
    	
    	this.strDocRoot      =  "" ;
    	this.strWebRoot      =  "" ;
    }
    
    /** ******************************************************************************** 
    * ���
    * @version 2008.10.16 
    * @author csj007
    * @param self_no String��ü.
    * @return  void 
    ********************************************************************************** */ 
    public void setGolfCardCoYn(String golfCardCo)                { this.golfCardCo = golfCardCo; }
    /** ******************************************************************************** 
    * ���
    * @version 2008.10.16 
    * @author csj007
    * @param self_no String��ü.
    * @return  void 
    ********************************************************************************** */ 
    public String getGolfCardCoYn()    { return this.golfCardCo;}
/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @param self_no String��ü.
* @return  void 
********************************************************************************** */ 
    public void setMemId(String memId)                { this.memId = memId; }

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @param self_no String��ü.
* @return  void 
********************************************************************************** */ 
    public void setMemNo(String memNo)                { this.memNo = memNo; }    

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @param self_no String��ü.
* @return  void 
********************************************************************************** */ 
    public void setMemNm(String memNm)                { this.memNm = memNm; }

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @param self_no String��ü.
* @return  void 
********************************************************************************** */ 
    public void setMemClss(String memClss)                { this.memClss = memClss; }

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @param self_no String��ü.
* @return  void 
********************************************************************************** */ 
    public void setLogin(boolean login)                { this.login = login; }



/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemId()    { return this.memId;}
    
/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemNo()    { return this.memNo;}   

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemNm()    { return this.memNm;}

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public String getMemClss()    { return this.memClss;}

/** ******************************************************************************** 
* ���
* @version 2008.10.16 
* @author csj007
* @return  String 
********************************************************************************** */ 
    public boolean isLogin()    { return this.login;}

    /**
	 * @info DocRoot ������
	 * @param String param
	 * @return void
	 */
	public void setDocRoot(String param)    { this.strDocRoot    = param; }
    /**
	 * @info DocRoot ������
	 * @return String
	 */
	public String getDocRoot()    { return strDocRoot   ; }
	 /**
	 * @info DocRoot ������
	 * @param String param
	 * @return void
	 */
	public void setWebRoot(String param)    { this.strWebRoot    = param; }
    /**
	 * @info DocRoot ������
	 * @return String
	 */
	public String getWebRoot()    { return strWebRoot   ; }
    
	 /**
	 * @info DocRoot ������
	 * @return String
	 */
	public String getTopMenu()    { return topMenu   ; }	
	/**
	 * @info DocRoot ������
	 * @return String
	 */
	public String getLeftMenu()    { return leftMenu   ; }	
}
