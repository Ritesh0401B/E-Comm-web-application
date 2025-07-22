package com.ecommerce.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "user")
@Entity
@Table(name = "my_order")
public class MyOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String orderId;

	private LocalDateTime orderDate;

//	@ManyToOne
//	private Product product;

	private Double price;

//	private int quantity;

	@ManyToOne
	private User user;

	private String paymentStatus;

	private String orderStatus;

	private String paymentType;

	private String paymentId;

	private String receipt;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private OrderAddress2 orderAddress2;

	@OneToMany(mappedBy = "myOrder", cascade = CascadeType.ALL)
	private List<OrderedProduct> orderedProducts;

}
