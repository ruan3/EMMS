package com.esquel.epass.utils;

public class BuildConfig {
	
	public enum ServerEndPoint {
		GAOMING,
		PRODUCTION,
		DEVELOPMENT,
		UAT,
		AZURE_UAT
	}
	//设置UAT还是PRD
	public static final ServerEndPoint endPoint = ServerEndPoint.PRODUCTION;
	//UAT dev后缀不存在了
	//如果什么都没有就是prd的环境
	private static final String gaomingAPIEndPoint = "http://esquelpassdev.esquel.cn/esquelpass/api/";
	private static final String gaomingConfigurationEndPoint = "http://esquelpassdev.esquel.cn/config/esquel/esquelpass/";
	private static final String gaomingContentServerEndPoint = "http://esquelpassdev.esquel.cn/esquelpasscontent/";
	private static final String productionAPIEndPoint = "https://esquelpass.esquel.cn/esquelpass/api/";
	private static final String productionConfigurationEndPoint = "https://esquelpass.esquel.cn/config/esquel/esquelpass/";
	private static final String productionContentServerEndPoint = "http://esquelpasscont.esquel.cn/esquelpasscontent/";
//	private static final String developmentAPIEndPoint = "http://esquelpassappweb.chinacloudsites.cn/esquelpass/api/";
//	private static final String developmentAPIEndPoint = "http://getazdevlnx002.chinacloudapp.cn/esquelpass/api/";
	private static final String developmentAPIEndPoint = "http://10.231.131.21:8080/esquelpass/api/";
//	private static final String developmentConfigurationEndPoint = "http://getazdevlnx002.chinacloudapp.cn/config/esquel/esquelpass/";
	private static final String developmentConfigurationEndPoint = "http://10.231.131.21:8080/config/esquel/esquelpass/";
//	private static final String developmentAPIEndPoint = "http://192.168.1.120:8080/esquelpass/api/";
//	private static final String developmentConfigurationEndPoint = "http://192.168.1.120:8080/config/esquel/esquelpass/";
//	private static final String developmentConfigurationEndPoint = "http://esquelpassappweb.chinacloudsites.cn/config/esquel/esquelpass/";

//	private static final String developmentAPIEndPoint = "http://139.219.140.66/esquelpass/api";
//	private static final String developmentAPIEndPoint = "http://getazdevlnx002.chinacloudapp.cn/esquelpass/api/";
//	private static final String developmentConfigurationEndPoint = "http://getazdevlnx002.chinacloudapp.cn/config/esquel/esquelpass/1/config.json";
//	private static final String developmentContentServerEndPoint = "http://getazdevlnx003.chinacloudapp.cn/esquelpasscontent/";
	private static final String developmentContentServerEndPoint = "http://10.231.131.21:8080/esquelpasscontent/";
//	private static final String developmentContentServerEndPoint = "http://192.168.1.120:8080/esquelpasscontent/";
//	private static final String developmentContentServerEndPoint = "http://getazdevlnx003.chinacloudapp.cn/esquelpasscontent/";
//	private static final String uatAPIEndPoint = "https://esquelpassuat.esquel.cn/esquelpass/api/";
	private static final String uatAPIEndPoint = "http://10.231.131.21:8080/esquelpass/api/";
//	private static final String uatAPIEndPoint = "http://getazdevlnx002.chinacloudapp.cn/esquelpass/api/";
//	private static final String uatConfigurationEndPoint = "http://getazdevlnx002.chinacloudapp.cn/config/esquel/esquelpass/";
	private static final String uatConfigurationEndPoint = "http://10.231.131.21:8080/config/esquel/esquelpass/";
//	private static final String uatConfigurationEndPoint = "http://esquelpassuat.esquel.cn/config/esquel/esquelpass/";
//	private static final String uatContentServerEndPoint = "http://getazdevlnx003.chinacloudapp.cn/esquelpasscontent/";
	private static final String uatContentServerEndPoint = "http://10.231.131.21:8080/esquelpasscontent/";
//	private static final String uatContentServerEndPoint = "http://esquelpasscont.esquel.cn/esquelpasscontent/";
	private static final String azureUATAPIEndPoint = "http://esquelpassappweb.chinacloudsites.cn/esquelpass/api/";
	private static final String azureUATContentServerEndPoint = "http://esquelpassconweb.chinacloudsites.cn/esquelpasscontent/api/";
	private static final String azureUATConfigurationEndPoint = "http://esquelpassappweb.chinacloudsites.cn/config/esquelpass/";
	
	public static String getConfigurationEndPoint() {
		switch (endPoint) {
		case GAOMING:
			return gaomingConfigurationEndPoint;
		case PRODUCTION:
			return productionConfigurationEndPoint;	
		case DEVELOPMENT:
			return developmentConfigurationEndPoint;
		case UAT:
			return uatConfigurationEndPoint;
		case AZURE_UAT:
			return azureUATConfigurationEndPoint;
		default:
			return productionConfigurationEndPoint;	
		}
	}
	
	public static String getContentServerEndPoint() {
		switch (endPoint) {
		case GAOMING:
			return gaomingContentServerEndPoint;
		case PRODUCTION:
			return productionContentServerEndPoint;	
		case DEVELOPMENT:
			return developmentContentServerEndPoint;
		case UAT:
			return uatContentServerEndPoint;	
		case AZURE_UAT:
			return azureUATContentServerEndPoint;
		default:
			return productionContentServerEndPoint;	
		}
	}
	
	public static String getServerAPIEndPoint() {
		switch (endPoint) {
			case GAOMING:
				return gaomingAPIEndPoint;
			case PRODUCTION:
				return productionAPIEndPoint;
			case DEVELOPMENT:
				return developmentAPIEndPoint;
			case UAT:
				return uatAPIEndPoint;
			case AZURE_UAT:
				return azureUATAPIEndPoint;
			default:
				return productionAPIEndPoint;	
		}
	}
	
	

}
