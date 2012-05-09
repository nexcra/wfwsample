/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : JtPagingStackProc
*   작성자    : (주)미디어포스 진현구
*   내용      : jolt paging common
*   적용범위  : golf
*   작성일자  : 2009-04-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*	2008.11.28	2008.12.03	2008.12.03	2008.12.03	hklee	모바일 적용 관련 페이징 변경 적용
***************************************************************************************************/
package com.bccard.golf.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.golf.common.JtPageNav;
import com.bccard.golf.common.JtPagingStackException;

/**
* jolt paging common class
* @version 2005.12.16
* @author 안웅식
*/
public class JtPagingStackProc extends AbstractObject {
	private static StringBuffer prev = null;
	private static StringBuffer midl = null;
	private static StringBuffer next = null;
	private static StringBuffer prev_custom = null;
	private static StringBuffer midl_custom = null;
	private static StringBuffer next_custom = null;
	private static String prev_url = "${PREV_URL}";
	private static String next_url = "${NEXT_URL}";
	private static String custom_link = "${CUSTOM_LINK}";

	/**
    * 페이지 조회키 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지 조회키 String
    */
	public String getJtPageInquiryKey(HttpServletRequest request, RequestParser parser, String initPageKey)
		          throws BaseException
		                 , JtPagingStackException {
		HttpSession sess = request.getSession( false );
		String keyType    = parser.getParameter("key_type", "INIT");
		String inquiryKey = parser.getParameter("next_key", initPageKey );

		try {
			JtPageNav pager = null;
			if ("INIT".equals(keyType)) {
				pager = new JtPageNav(initPageKey, keyType);
			} else if ("NEXT".equals(keyType)) {
				pager = (JtPageNav)sess.getAttribute("JT_PAGE_KEY_SET");
				pager.setKey(inquiryKey);
			} else if ("PREV".equals(keyType)) {
				pager = (JtPageNav)sess.getAttribute("JT_PAGE_KEY_SET");
				inquiryKey = pager.getKey();
			} else {
				throw new JtPagingStackException();
			}

			sess.removeAttribute("JT_PAGE_KEY_SET");
			sess.setAttribute(   "JT_PAGE_KEY_SET", pager);
		} catch ( Exception e ) {
			//	e.setKey( "JT_PAGING_PROC");
			//	throw e;
			throw new BaseException("JT_PAGING_PROC");
		}

		return inquiryKey;
	}

	/**
    * 페이지 조회키 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지 조회키 String
    */
	public String getJtPageInquiryKey(HttpServletRequest request
										, RequestParser parser
										, String initPageKey
										, boolean forceInit) throws BaseException, JtPagingStackException {
		HttpSession sess = request.getSession( false );
		String keyType    = forceInit ? parser.getParameter("key_type", "INIT") : "INIT";
		String inquiryKey = parser.getParameter("next_key", initPageKey );

		if (forceInit) {
			keyType    = parser.getParameter("key_type", "INIT");
			inquiryKey = parser.getParameter("next_key", initPageKey );
		} else {
			keyType    = "INIT";
			inquiryKey = initPageKey;
		}
debug("[JtPagingStackProc] sess :: "  + sess);
debug("[JtPagingStackProc] keyType :: "  + keyType);

		try {
			JtPageNav pager = null;
			if ("INIT".equals(keyType)) {
				pager = new JtPageNav(initPageKey, keyType);
			} else if ("NEXT".equals(keyType)) {
				pager = (JtPageNav)sess.getAttribute("JT_PAGE_KEY_SET");
				// 모바일 서비스용 (sesion 대신 request 처리
				if ( request instanceof com.bccard.waf.action.ServiceRequest && pager == null) {
					pager = new JtPageNav(initPageKey, keyType);
				}
				pager.setKey(inquiryKey);
			} else if ("PREV".equals(keyType)) {
				pager = (JtPageNav)sess.getAttribute("JT_PAGE_KEY_SET");


				// 모바일 서비스용 (sesion 대신 request 처리
				if ( request instanceof com.bccard.waf.action.ServiceRequest && pager == null) {
					inquiryKey = parser.getParameter("prev_key", initPageKey );
					pager = new JtPageNav(inquiryKey, keyType);
				} else {
					inquiryKey = pager.getKey();
				}
			} else {
				pager = new JtPageNav(initPageKey, keyType);
			}

			sess.removeAttribute("JT_PAGE_KEY_SET");
			sess.setAttribute(   "JT_PAGE_KEY_SET", pager);
		} catch ( Exception e ) {
			//	e.setKey( "JT_PAGING_PROC");
			//	throw e;

debug("Exception MESSAGE : " + e.getMessage());
			throw new BaseException("JT_PAGING_PROC");
		}

		return inquiryKey;
	}

	/**
    * 페이지 네비게이션 html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지 네비게이션 html  String
    */
	public String getDefaultPageNavigationHtml(HttpSession session, boolean nextFlag) {
		this.generateDefaultBuffer();
		StringBuffer buff = new StringBuffer();
		JtPageNav pager = (JtPageNav)session.getAttribute("JT_PAGE_KEY_SET");
		if (pager.isPrev()) { buff.append(prev); }
		buff.append(midl);
		if (nextFlag) { buff.append(next); }
		return buff.toString();

	}

	/**
    * 페이지 네비게이션 html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지 네비게이션 html  String
    */
	public String generateCustomPageNavigationBuffer(HttpSession session, boolean nextFlag) {
		this.generateCustomBuffer();
		JtPageNav pager = (JtPageNav)session.getAttribute("JT_PAGE_KEY_SET");
		StringBuffer buff = new StringBuffer();
		if (pager.isPrev()) { buff.append(prev_custom); }
		buff.append(midl_custom);
		if (nextFlag) { buff.append(next_custom); }
		return buff.toString();
	}

	/**
    * 이전 페이지  html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 이전 페이지  html  String
    */
	public String generatePrevUrl(String crude, String prevUrl) {
		return StrUtil.replace(crude, prev_url, prevUrl);
	}


	/**
    * 페이지  html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지  html  String
    */
	public String generateCustomLink(String crude, String link) {
		return StrUtil.replace(crude, custom_link, link);
	}

	/**
    * 다음 페이지  html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 다음 페이지  html  String
    */
	public String generateNextUrl(String crude, String nextUrl) {
		return StrUtil.replace(crude, next_url, nextUrl);
	}

	/**
    * 페이지  html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지  html  String
    */
	private void generateDefaultBuffer() {
		if (null == prev) {
			prev = new StringBuffer();
			prev.append("<a href=\"javascript:prev_submit();\">");
			prev.append("	<img src=\"/golf/img/common/bt4_prev.gif\" align=\"absmiddle\" alt=\"이전\"/>");
			prev.append("</a>");
		}

		if (null == midl) {
			midl = new StringBuffer();
			midl.append("<li></li>");
		}

		if (null == next) {
			next = new StringBuffer();
			next.append("<a href=\"javascript:next_submit();\">");
			next.append("	<img src=\"/golf/img/common/bt4_next.gif\" align=\"absmiddle\" alt=\"다음\"/>");
			next.append("</a>");
		}
	}

	/**
    * 페이지  html 조회
    * @version 2005.12.16
 	* @author 안웅식
    * @return 페이지  html  String
    */
	private void generateCustomBuffer() {
		if (null == prev_custom) {
			prev_custom = new StringBuffer();
			prev_custom.append("<a href=\""+prev_url+"\">");
			prev_custom.append("	<img src=\"/golf/img/common/bt4_prev.gif\" align=\"absmiddle\" alt=\"이전\"/>");
			prev_custom.append("</a>");
		}

		if (null == midl_custom) {
			midl_custom = new StringBuffer();

			if(!"${CUSTOM_LINK}".equals(custom_link)) {
            	midl_custom.append("<li>");
            	midl_custom.append(custom_link);
            	midl_custom.append("</li>");
            }
		}

		if (null == next_custom) {

			next_custom = new StringBuffer();
			next_custom.append("<a href=\""+next_url+"\">");
			next_custom.append("	<img src=\"/golf/img/common/bt4_next.gif\" align=\"absmiddle\" alt=\"다음\"/>");
			next_custom.append("</a>");
		}
	}
}