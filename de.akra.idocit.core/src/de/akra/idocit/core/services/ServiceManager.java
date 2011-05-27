package de.akra.idocit.core.services;

import de.akra.idocit.nlp.stanford.services.WSDLTaggingService;

public class ServiceManager
{
	private static final ServiceManager instance = new ServiceManager();

	private ParsingService parsingService = new ParsingService();

	private PersistenceService persistenceService = new PersistenceService();

	private ThematicGridService thematicGridService = new ThematicGridService();

	private WSDLTaggingService wsdlTaggingService = null;

	private ServiceManager()
	{

	}

	public WSDLTaggingService getWsdlTaggingService()
	{
		return wsdlTaggingService;
	}

	public void setWsdlTaggingService(WSDLTaggingService wsdlTaggingService)
	{
		this.wsdlTaggingService = wsdlTaggingService;
	}

	public static ServiceManager getInstance()
	{
		return instance;
	}

	public ParsingService getParsingService()
	{
		return parsingService;
	}

	public PersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	public ThematicGridService getThematicGridService()
	{
		return thematicGridService;
	}
}
