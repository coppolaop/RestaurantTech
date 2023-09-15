package com.darksun.controller.ai.tools;

import com.darksun.model.OrderCard;
import com.darksun.model.OrderItem;
import com.darksun.service.OrderCardService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderCardTools {

	@Autowired
	OrderCardService service;

	@Tool
	public OrderCard createOrderCard( ) {
		return service.create( 0 );
	}

	@Tool
	public OrderCard readOrderCardById( Long id ) {
		OrderCard orderCard = service.readById( id );

		for ( OrderItem item : orderCard.getItems( ) ) {
			item.setOrderCard( null );
		}

		return orderCard;
	}

	@Tool
	public BigDecimal orderCardPrice( Long id ) {
		return service.getFinalPrice( id );
	}

	@Tool
	public void payment( Long id, Boolean wasCredit ) {
		service.pay( id, wasCredit );
	}
}
