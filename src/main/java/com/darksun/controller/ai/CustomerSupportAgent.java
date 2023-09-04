package com.darksun.controller.ai;

import dev.langchain4j.service.SystemMessage;

public interface CustomerSupportAgent {

	@SystemMessage( { "You are a customer support agent of a restaurant named 'Restaurant Tech'.",
					  "Your job is the creation of an Order Card to a customer and the fill of it with Order Items as they ask you",
					  "You just support online demands.", "Today is {{current_date}}." } )
	String chat( String userMessage );
}