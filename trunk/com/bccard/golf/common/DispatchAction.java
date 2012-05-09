/*
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� ���� : 2007. 11. 29 [hsbang@intermajor.com]
 */
package com.bccard.golf.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.loginAction.BaseAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
/**
 * �������� Action Ŭ������ �� Ŭ������ ������ ��Ȱ�� �Ѵ�.<br>
 * ���� ������ �ִ� ��ɵ��� �ϳ��� Action Ŭ�������� ����.<br>
 * DispatchAction �� ����ϱ� ���ؼ��� Action ���� ������ ������ ���� �����ؾ� �Ѵ�.<br>
 * 
 * <pre>
 *  &lt;action key=&quot;TestBbsList&quot; class=&quot;com.bccard.shop.sample.board.BbsAction&quot; desc=&quot;���� �Խ��� ���&quot;&gt;
 *  &lt;action_param key=&quot;METHOD&quot; value=&quot;list&quot;/&gt;
 *  &lt;response key=&quot;default&quot; type=&quot;forward&quot; content=&quot;/view/sample/board/list.jsp&quot;/&gt;
 *  &lt;/action&gt;
 * </pre>
 * 
 * key ���� "METHOD"�� value ���� ȣ���� �޼ҵ���� �ȴ�.
 * 
 * @author hsbang 
 * @version 2007. 11. 29
 */ 
public class DispatchAction extends BaseAction {
	
	protected HashMap methods = new HashMap();

	protected Class clazz = this.getClass();

	protected Class[] types = { com.bccard.waf.core.WaContext.class, HttpServletRequest.class, HttpServletResponse.class };

	/**
	 * 
	 * @see com.bccard.shop.common.action.BaseAction#execute(com.bccard.waf.core.WaContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		//debug("DispatchAction Start");
		//debug("Context: "+context);
		return dispatchMethod(context, request, response);
	}

	/**
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse dispatchMethod(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		Method method = null;
		String name = null;
		try {
			name = getActionParam(context, "METHOD");
//debug("name:"+name);
			method = getMethod(name);
//debug("DispatchAction ActionKey = " + getActionKey(context));
//debug("DispatchAction METHOD = " + name);
		} catch (NoSuchMethodException e) {
			throw new BaseException("NoSuchMethodException", new String[] {}, e);
		}

		ActionResponse actionResponse = null;
		try {
			Object args[] = { context, request, response };
			actionResponse = (ActionResponse) method.invoke(this, args);
		} catch (ClassCastException e) {
//debug("ClassCastException :"+e.getMessage());
			throw new BaseException("ClassCastException", new String[] {}, e);
		} catch (IllegalAccessException e) {
//debug("IllegalAccessException :"+e.getMessage());
			throw new BaseException("IllegalAccessException", new String[] {}, e);
		} catch (InvocationTargetException e) {
//debug("InvocationTargetException :"+e.getMessage());
			Throwable t = e.getTargetException();
			if (t instanceof BaseException) {
				throw ((BaseException) t);
			} else {
				error("dispatch.error", e);
				throw new ServletException(t);
			}
		}catch(Exception e){
debug("Exception :"+e.getMessage());		
		}
		return actionResponse;
	}

	/**
	 * @param name
	 * @return
	 * @throws NoSuchMethodException
	 */
	protected Method getMethod(String name) throws NoSuchMethodException {
		synchronized (methods) {
			Method method = (Method) methods.get(name);

			if (method == null) {
				method = clazz.getMethod(name, types);
				methods.put(name, method);
			}

			return (method);
		}
	}

}
