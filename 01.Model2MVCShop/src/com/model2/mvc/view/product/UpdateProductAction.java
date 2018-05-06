package com.model2.mvc.view.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.user.UserService;
import com.model2.mvc.service.user.impl.UserServiceImpl;

public class UpdateProductAction extends Action{
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int prodNo = Integer.parseInt(request.getParameter("prodNo"));
		String menu = request.getParameter("menu");

		Product product = new Product();
		product.setProdNo(prodNo);
		product.setProdName(request.getParameter("prodName"));
		product.setProdDetail(request.getParameter("prodDetail"));
		product.setManuDate(request.getParameter("manuDate").replaceAll("-", ""));
		product.setPrice(Integer.parseInt(request.getParameter("price")));
		product.setFileName(request.getParameter("fileName"));
		product.setProdCnt(Integer.parseInt(request.getParameter("prodCnt")));

		ProductService service = new ProductServiceImpl();
		service.updateProduct(product);
		System.out.println("Update ³¡");
		/*HttpSession session = request.getSession();
		String sessionId = ((User) session.getAttribute("user")).getUserId();

		if (sessionId.equals(product)) {
			session.setAttribute("product", product);
		}*/

		return "redirect:/getProduct.do?prodNo=" + prodNo + "&menu=" + menu;
	}
}
