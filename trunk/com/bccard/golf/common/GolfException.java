/** ****************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 : csj007
 * 내용 : 지방세 통합 시스템 예외
 ************************** 수정이력 *******************************************
 *    일자      버전   작성자   변경사항
 *
 **************************************************************************** */
package com.bccard.golf.common;

import java.io.Serializable;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.common.BaseException;


/******************************************************************************
* 지방세 통합관리 시스템 예외.
* @author csj007
* @version 2008.08.06
******************************************************************************/
public class GolfException extends BaseException implements MsgHandler,Serializable {
    /** 메시지정보 엔티티 */ private MsgEtt msgEtt;

    /** ***********************************************************************
    * 시스템 예외.
    * @param ett 메시지정보를 담은 MsgEtt
    ************************************************************************ */
    public GolfException(MsgEtt ett) {
        super(ett.getMessage());
        this.msgEtt = ett;
    }

    /** ***********************************************************************
    * 메시지정보 를 통한 예외.
    * @param ett 메시지정보를 담은 MsgEtt
    ************************************************************************ */
    public GolfException(MsgEtt ett,Throwable t) {
        this(ett);
        super.setRootCause(t);
    }

    /** ***********************************************************************
     * 시스템 에러, 사용자 정의 에러 분기 예외
     * @param t Throwable
     ************************************************************************ */
     public GolfException(Throwable t) {
    	 this("", t);
     }
     
    /** ***********************************************************************
     * 시스템 에러, 사용자 정의 에러 분기 예외
     * @param title 액션 타이틀
     * @param t Throwable
     ************************************************************************ */
     public GolfException(String title, Throwable t) {
 		super.setRootCause(t);
 		if ( t instanceof MsgHandler ) {
 			this.msgEtt = ((MsgHandler)t).getMsgEtt();
 			if (title == null || title.compareTo("") == 0) { this.msgEtt.setTitle(title); }
 		} else {
 			this.msgEtt = new MsgEtt( MsgEtt.TYPE_ERROR, title, "SYSTEM_ERROR", null );
 		}
     }
     
     /** ***********************************************************************
      * 시스템 에러, 사용자 정의 에러 분기 예외
      * @param title 액션 타이틀
      * @param msgkey 메시지키
      * @param t Throwable
      ************************************************************************ */
      public GolfException(String title, String msgkey, Throwable t) {
  		if ( t != null ) { super.setRootCause(t); }
		this.msgEtt = new MsgEtt( MsgEtt.TYPE_ERROR, title, msgkey, null );
      }     

    /** ***********************************************************************
    * 메시지정보 엔티티 클래스 반환.
    * @return 메시지정보를 담은 MsgEtt 반환
    ************************************************************************ */
    public MsgEtt getMsgEtt() {
        if ( this.msgEtt == null ) {
            this.msgEtt = new MsgEtt();
            this.msgEtt.setType( MsgEtt.TYPE_ERROR );
            this.msgEtt.setTitle("");
            this.msgEtt.setMessage( getMessage() );
        }
        return this.msgEtt;
    }

}