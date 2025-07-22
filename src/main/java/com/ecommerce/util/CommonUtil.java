package com.ecommerce.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecommerce.entities.MyOrder;
import com.ecommerce.entities.OrderedProduct;
import com.ecommerce.services.MyOrderService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	public Boolean sendEmail(String url, String reciepentEmail)
			throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setTo(reciepentEmail);

		helper.setFrom("kushwaharitesh123456@gmail.com", "Shopping Cart");

		helper.setSubject("Password Reset");

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";

		helper.setText(content, true);

		mailSender.send(message);

		return true;

	}

	public static String generateUrl(HttpServletRequest request) {

		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");

	}

	String msg = null;

	public boolean sendMailForProductOrder(MyOrder order, String status) throws Exception {

		System.out.println(status);

		try {

			List<OrderedProduct> orderedProducts = order.getOrderedProducts();

			StringBuilder productDetails = new StringBuilder();

			for (OrderedProduct op : orderedProducts) {
				productDetails.append("<hr/>");
				productDetails.append("<p><b>Product Name:</b> " + op.getProduct().getTitle() + "</p>");
				productDetails.append("<p><b>Category:</b> " + op.getProduct().getCategory() + "</p>");
				productDetails.append("<p><b>Quantity:</b> " + op.getQuantity() + "</p>");
				productDetails.append("<p><b>Price:</b> ₹" + op.getProduct().getDiscountPrice() + "</p>");
			}

			msg = "<p>Hello [[name]],</p>" + "<p>Thank you order is <b>[[orderStatus]]</b>.</p>"
					+ "Order Id : [[orderId]]" + "<p>Payment Type : [[paymentType]]</p>" + "<p><b>Product Details:</b></p>"
					+ productDetails.toString() + "<hr/>" + "<p>Total Amount : [[totalPrice]]</p>";

			msg = msg.replace("[[name]]", order.getOrderAddress2().getFirstName());
			msg = msg.replace("[[orderStatus]]", status);
			msg = msg.replace("[[paymentType]]", order.getPaymentType());
			msg = msg.replace("[[totalPrice]]", order.getPrice().toString());
			msg = msg.replace("[[orderId]]", order.getOrderId());

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom("kushwaharitesh123456@gmail.com", "Shopping Cart");

			String email1 = order.getOrderAddress2().getEmail();
			String email2 = order.getUser().getEmail();

			helper.setTo(order.getOrderAddress2().getEmail());

			if (email1 != email2) {
				helper.setTo(new String[] { email1, email2 });
			} else {
				helper.setTo(email1);
			}

			helper.setSubject("Product Order Status");
			helper.setText(msg, true);

			mailSender.send(message);
			System.out.println("Mail sent successfully");
			System.out.println("to this email = " + order.getOrderAddress2().getEmail());

			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

}
