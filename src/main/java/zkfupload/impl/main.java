package zkfupload.impl;

import java.net.URL;

public class main {
	public static void main(String[] args) throws Exception {
		ZKFRaiser.newRaiser()
		.filePath(GetResource.getPath("example.conf"))
		.cluster("122.225.114.27:2181,122.225.114.24:2181,122.225.114.25:2181")
		.node("/flume/collector")
		.connect()
		.upload();
	}
}

class GetResource{
	public static String getPath(String fileName){
		return GetResource.class.getClassLoader().getResource(fileName).getPath();
	
	}
}
