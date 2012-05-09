/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : DbTao 처리시 발생하는 예외
* 수정 :
* 내용 :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.tao.TaoException;

/** ***************************************************************************
* DbTao 처리시 발생하는 예외
* @author 조용국
* @version 2007.01.04
**************************************************************************** */
public class DbTaoException extends TaoException implements MsgHandler,Serializable {
    /** 메시지정보 엔티티 */ private MsgEtt msgEtt;

    /** ***********************************************************************
    * 메시지를 통한 예외.
    * @param s 메시지
    ************************************************************************ */
    public DbTaoException(String s, Throwable t) {
        super(s);
        super.setRootCause(t);
    }

    /** ***********************************************************************
    * 메시지정보를 통한 예외.
    * @param ett 메시지정보를 담은 MsgEtt
    ************************************************************************ */
    public DbTaoException(MsgEtt ett) {
        super(ett.getMessage());
        this.msgEtt = ett;

		super.setKey(ett.getKey());
    }

    /** ***********************************************************************
    * 메시지정보 를 통한 예외.
    * @param ett 메시지정보를 담은 MsgEtt
    ************************************************************************ */
    public DbTaoException(MsgEtt ett,Throwable t) {
        this(ett.getMessage(),t);
        this.msgEtt = ett;

		super.setKey(ett.getKey());
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
