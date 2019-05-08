package org.zerhusen.contollers.ams;

import java.util.List;

import org.apache.http.HttpException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.payload.PaymentPagePayload;

import com.instamojo.wrapper.api.ApiContext;
import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.api.InstamojoImpl;
import com.instamojo.wrapper.exception.ConnectionException;
import com.instamojo.wrapper.exception.HTTPException;
import com.instamojo.wrapper.filter.PaymentRequestFilter;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.model.*;
import com.instamojo.wrapper.response.ApiListResponse;


@RestController
@CrossOrigin(origins="*")
@RequestMapping("/payments-instamojo")
public class PaymetInstamojoRest {

	ApiContext context = ApiContext.create("test_fJEPvVj8xs9l0dQw4VZPY5aktpKldUAilWc", "test_2ZW3fILEKuDDTCnuKYDVmDKDI7TGjuqzcVRIu87YbbirCCBf9BKSWw2I8sJ1t6scPhz5xtonhqiirkDpO3xdU3o02cxIiKk29d9DFyTQ0SFCyhzW4GZKtqtXKdl", ApiContext.Mode.TEST);
	Instamojo api = new InstamojoImpl(context);
	
	
	@GetMapping("/getCountPages")
	public PaymentPagePayload getPageCount() {
		try {
			int count = this.api.getPaymentOrders(1, 500).getCount();
			
			PaymentPagePayload page = new PaymentPagePayload(count);
			return page;
		}catch (HTTPException e) {
			return null;
		}catch (ConnectionException e) {
			return null;
		}
	}
	
	
	@GetMapping("/allPayments/{page}")
	public @ResponseBody Iterable<PaymentOrder> getAllTransactions(@PathVariable("page") int page) {
		
		
		try {
			
			
			ApiListResponse<PaymentOrder> paymentOrder =  this.api.getPaymentOrders(page, 50);


			return paymentOrder.getResults();
		}catch (HTTPException e) {
			System.out.println(e.getStatusCode());
		    System.out.println(e.getMessage());
		    System.out.println(e.getJsonPayload());
		    return null;
		}catch (ConnectionException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
	}
}
