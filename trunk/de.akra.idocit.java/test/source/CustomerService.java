package source;

import java.util.List;

public interface CustomerService
{
	/**
	 * Only customers who placed an order within the last year are considered.
	 * 
	 * @param parameters
	 *            ->firstname [COMPARISON]
	 *            ->lastname [COMPARISON]
	 * 
	 * @return [OBJECT]
	 * @source CRM System
	 * @ordering Alphabetically by lastname 
	 */
	public List<Customer> findCustomersByName(NameParameters parameters);
}
