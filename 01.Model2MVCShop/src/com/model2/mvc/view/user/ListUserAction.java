package com.model2.mvc.view.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;


public class ListUserAction extends Action {

	@Override
	public String execute(	HttpServletRequest request,
												HttpServletResponse response) throws Exception {
		Search search=new Search();
		
		System.out.println("SearchCondition : "+request.getParameter("searchCondition"));
		System.out.println("searchKeyword : "+request.getParameter("searchKeyword"));
		
		int page=1;
		if(request.getParameter("currentPage") != null) {
			if(!request.getParameter("currentPage").equals("")) {
				page=Integer.parseInt(request.getParameter("currentPage"));
			}
		}
		
		search.setPage(page);
		search.setSearchCondition(request.getParameter("searchCondition"));
		search.setSearchKeyword(request.getParameter("searchKeyword"));
		
		int pageUnit=Integer.parseInt(getServletContext().getInitParameter("pageUnit"));
		int pageSize=Integer.parseInt(getServletContext().getInitParameter("pageSize"));
		search.setpageSize(pageSize);
		
		UserService service=new UserServiceImpl();
		Map<String,Object> map=service.getUserList(search);
		
		Page resultPage = new Page(page, ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println("ListUserAction ::"+resultPage);

		request.setAttribute("list", map.get("list"));
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("search", search);
		
		return "forward:/user/listUser.jsp";
	}
}