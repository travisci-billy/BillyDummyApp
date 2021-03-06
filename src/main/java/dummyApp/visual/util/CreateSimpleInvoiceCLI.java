package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTCustomerEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTProductEntity;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;
import com.premiumminds.billy.portugal.services.entities.PTPayment;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice.CLIENTTYPE;

import dummyApp.app.AppManager;

public class CreateSimpleInvoiceCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateSimpleInvoiceCLI(AppManager manager) {
		this.manager = manager;
	}

	public PTSimpleInvoice createInvoice() {
		PTProductEntity product;
		PTBusinessEntity business;
		PTCustomerEntity customer;
		String productName, businessName, customerName;
		BigDecimal quantity, price, total = new BigDecimal(0);
		CLIENTTYPE type = null;

		try {
			System.out.println("Business Name:");
			businessName = bufferReader.readLine();

			business = (PTBusinessEntity) manager.getAppCLI()
					.getBusinessByName(businessName);

			if (business == null) {
				System.out.println("Business not found, create new? (y/n)");
				String answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					business = (PTBusinessEntity) new CreateBusinessCLI(manager)
							.createBusiness();
					manager.getAppCLI().getBusinesses().add(business);
				} else {
					return null;
				}
			}

			System.out.println("Customer Name:");
			customerName = bufferReader.readLine();

			if (customerName.equals("")) {

				customer = (PTCustomerEntity) manager.endCustomer();
				if (!(manager.getAppCLI().getCustomers().contains(customer))) {
					manager.getAppCLI().getCustomers().add(customer);
				}
				type = CLIENTTYPE.CUSTOMER;
			} else {
				String answer;
				customer = (PTCustomerEntity) manager.getAppCLI()
						.getCustomerByName(customerName);
				if (customer == null) {
					System.out.println("Customer not found, create new? (y/n)");
					answer = bufferReader.readLine();
					if (answer.toLowerCase().contains("y")) {
						customer = (PTCustomerEntity) new CreateCustomerCLI(
								manager).createCustomer();
						manager.getAppCLI().getCustomers().add(customer);
					}
				}
				System.out.println("Client type: (c-consumer/b-business)");
				answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("c")) {
					type = CLIENTTYPE.CUSTOMER;
				} else if (answer.toLowerCase().contains("b")) {
					type = CLIENTTYPE.BUSINESS;
				}
			}

			List<PTInvoiceEntry.Builder> entries = new ArrayList<PTInvoiceEntry.Builder>();
			while (true) {
				String answer;
				System.out.println("Product description:");
				productName = bufferReader.readLine();

				product = (PTProductEntity) manager.getAppCLI()
						.getProductByDescription(productName);

				if (product == null) {
					System.out.println("Product not found, create new? (y/n)");
					answer = bufferReader.readLine();
					if (answer.toLowerCase().contains("y")) {
						product = (PTProductEntity) new CreateProductCLI(
								manager).createProduct();
						manager.getAppCLI().getProducts().add(product);
					} else {
						return null;
					}
				}

				System.out.println("Quantity:");
				quantity = new BigDecimal(bufferReader.readLine());
				System.out.println("Price:");
				price = new BigDecimal(bufferReader.readLine());
				total = total.add(quantity).multiply(price);

				entries.add(manager
						.createInvoiceEntry(quantity, price, product));
				System.out.println("Finish adding products? (y/n)");
				answer = bufferReader.readLine();
				if (answer.toLowerCase().contains("y")) {
					break;
				}
			}
			PTPayment.Builder payment = manager.createPayment(total);

			PTSimpleInvoice simpleInvoice = manager.createSimpleInvoice(
					entries, payment, business, customer, type);
			if (simpleInvoice == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Simple Invoice: " + simpleInvoice.getNumber()
					+ " created.");
			System.out.println("Do you want to print a PDF? (y/n)");
			String answer = bufferReader.readLine();
			if (answer.toLowerCase().contains("y")) {
				manager.exportSimpleInvoicePDF(simpleInvoice.getUID());
			}
			return simpleInvoice;

		} catch (Exception e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		}
		return null;
	}

}
