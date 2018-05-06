package com.model2.mvc.view.purchase;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class GetPurchaseAction extends Action {

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String tranNo = request.getParameter("tranNo");
		System.out.println("tranNo : "+tranNo);
		
		PurchaseService service = new PurchaseServiceImpl();
		Map<String, Object> map = service.getPurchase(Integer.parseInt(tranNo));
		
		request.setAttribute("list", map.get("list"));

		return "forward:/purchase/GetPurchase.jsp";
	}
}
