package dummyApp.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.services.entities.PTProduct;

import dummyApp.app.AppManager;

public class CreateProductCLI {

	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
			System.in));
	AppManager manager;

	public CreateProductCLI(AppManager manager) {
		this.manager = manager;
	}

	public PTProduct createProduct() {

		String description, productCode, unitOfMeasure, iva;

		try {
			System.out.println("Description:");
			description = bufferReader.readLine();
			System.out.println("Product Code:");
			productCode = bufferReader.readLine();
			System.out.println("Unit of Measure:");
			unitOfMeasure = bufferReader.readLine();
			System.out.println("IVA: (6/13/23)");
			iva = bufferReader.readLine();			

			if (description.equals("")) {
				description = "Delta Café";
			}
			if (productCode.equals("")) {
				productCode = "56012345667";
			}
			if (unitOfMeasure.equals("")) {
				unitOfMeasure = "Kg";
			}
			if(iva.equals("")) {
				iva = "23";
			}
			
			UID tax;
			if(iva.equals("23")) {
				tax = manager.getTaxes().continent().normal().getUID();
			} else if(iva.equals("13")) {
				tax = manager.getTaxes().continent().intermediate().getUID();
			} else {
				tax = manager.getTaxes().continent().reduced().getUID();
			}

			PTProduct product = manager.createProduct(productCode, description,
					unitOfMeasure, tax);

			if (product == null) {
				System.out.println("Something went wrong");
			}
			System.out.println("Product: " + product.getDescription()
					+ " created.");
			return product;

		} catch (IOException e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		} catch (Exception e) {
			System.out.println("[ERROR] An error ocurred at: " + e.toString());
		}
		return null;

	}
}
