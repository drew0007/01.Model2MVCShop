package com.model2.mvc.view.product;

import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class GetProductAction extends Action {

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String prodNo = request.getParameter("prodNo");
		System.out.println("prodNo : "+prodNo);
		System.out.println(request.getParameter("menu"));
		
		ProductService service = new ProductServiceImpl();
		Product product = service.getProduct(Integer.parseInt(prodNo));
		
		System.out.println("========history==========");
		Cookie[] cookie = request.getCookies();
		String history = "";
		if(cookie != null) {
			for(int i=0; i<cookie.length; i++) {
				if(cookie[i].getName().equals("history")) {
					history = cookie[i].getValue();
					System.out.println("ÄíÅ° history : "+history);
					String[] h = history.split(",");
					for (int j = 0; j < h.length; j++) {
						if (h[j].equals(prodNo)) {
							break;
						}
						if(j == h.length-1) {
							history += ","+prodNo;
							System.out.println("Ãß°¡µÈ history : "+history);
						}
					}
				}
			}
		}
		if(history.equals("")) {
			history = prodNo;
		}
		System.out.println("history : "+history);
		
		Cookie c = new Cookie("history", history);
		c.setMaxAge(60*60);
		response.addCookie(c);
		
		
		request.setAttribute("product", product);
		
		return "forward:/product/getProduct.jsp";
	}
}
