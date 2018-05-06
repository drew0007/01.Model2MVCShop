package com.model2.mvc.view.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class HistoryPurchaseAction extends Action {

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String prodNo = request.getParameter("prodNo");
		System.out.println(prodNo);

		Search search=new Search();
		
		System.out.println("SearchCondition : "+request.getParameter("searchCondition"));
		System.out.println("searchKeyword : "+request.getParameter("searchKeyword"));
		
		int page = 1;
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
		
		PurchaseService service = new PurchaseServiceImpl();
		Map<String,Object> map = service.getSaleList(search, Integer.parseInt(prodNo));
		
		Page resultPage = new Page(page, ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println("ListPurchaseAction ::"+resultPage);

		request.setAttribute("list", map.get("list"));
		request.setAttribute("resultPage", resultPage);
		request.setAttribute("search", search);
		
		if(prodNo.equals("0")) {
			return "forward:/purchase/saleListPurchase.jsp";
		}else {
			return "forward:/purchase/historyPurchase.jsp";
		}
	}
}
