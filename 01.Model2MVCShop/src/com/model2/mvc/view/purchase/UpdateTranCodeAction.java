package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class UpdateTranCodeAction extends Action{
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int tranNo = Integer.parseInt(request.getParameter("tranNo"));
		int tranCnt = Integer.parseInt(request.getParameter("tranCnt"));
		String prodNo = request.getParameter("prodNo");
		String tranCode = request.getParameter("tranCode");
		
		Product product = new Product();
		product.setProdNo(Integer.parseInt(prodNo));
		
		Purchase purchase = new Purchase();
		purchase.setTranCode(tranCode);
		purchase.setTranNo(tranNo);
		purchase.setTranCnt(tranCnt);
		purchase.setPurchaseProd(product);

		PurchaseService service = new PurchaseServiceImpl();
		service.updateTranCode(purchase);

		String next = "";
		if (tranCode.equals("2") || tranCode.equals("5")) {
			next = "forward:/historyPurchase.do?prodNo=" + prodNo;
		}else {
			next = "forward:/listPurchase.do";
		}
		return next;
	}
}