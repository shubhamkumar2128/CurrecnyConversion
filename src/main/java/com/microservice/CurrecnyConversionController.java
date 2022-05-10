package com.microservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrecnyConversionController {
	@Autowired
	private CurrecnyExchangeProxy proxy;

	// Using restTemplate
	@GetMapping("currency-converison/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		HashMap<String, String> uriVar = new HashMap<>();
		uriVar.put("from", from);
		uriVar.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVar);
		CurrencyConversion conversion = responseEntity.getBody();

		return new CurrencyConversion(conversion.getId(), from, to, conversion.getConversionMultiple(), quantity,
				quantity.multiply(conversion.getConversionMultiple()), conversion.getEnvironment());
	}

	// Using Feign Client
	@GetMapping("currency-converison-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		CurrencyConversion conversion = proxy.retriveExchangeValue(from, to);

		return new CurrencyConversion(conversion.getId(), from, to, conversion.getConversionMultiple(), quantity,
				quantity.multiply(conversion.getConversionMultiple()), conversion.getEnvironment());
	}
}
