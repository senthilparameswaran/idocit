/*******************************************************************************
 * Copyright 2012 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package source;

import java.io.IOException;
import java.util.List;

public interface CustomerService
{
	public interface InnerCustomerService
	{
		public List<Customer> findCustomersByName(NameParameters parameters)
				throws IOException;
	}

	/**
	 * Only customers who placed an order within the last year are considered.
	 * 
	 * @ordering Alphabetically by lastname
	 * @source CRM System
	 * 
	 * @param parameters [COMPARISON]
	 * @subparam firstName [COMPARISON]
	 * @subparam lastName [COMPARISON]
	 * 
	 * @return [OBJECT]
	 * 
	 * @throws IOException  In case of an error
	 * @thematicgrid Searching Operations
	 */
	public List<Customer> findCustomersByName(NameParameters parameters)
			throws IOException;
	
	/**
	 * Only customers who placed an order within the last year are considered.
	 * 
	 * @ordering Alphabetically by lastname
	 * @source CRM System
	 * 
	 * @param parameters
	 * @subparam customer.firstName [COMPARISON]
	 * @subparam customer.lastName [COMPARISON]
	 * 
	 * @return [OBJECT]
	 * @subreturn firstName [ATTRIBUTE] Won't be null, but could be an empty String
	 * @subreturn lastName [ATTRIBUTE] Won't be null, but could be an empty String
	 * 
	 * @throws SpecialException  In case of an error
	 * @thematicgrid Searching Operations
	 */
	public Customer findCustomerByName(CustomerNameParameters parameters)
			throws SpecialException;
}
