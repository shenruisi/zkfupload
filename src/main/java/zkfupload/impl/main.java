package zkfupload.impl;

import java.net.URL;

public class main {
	public static void main(String[] args) throws Exception {
		ZKFRaiser.newRaiser()
		.filePath(GetResource.getPath("example.conf"))
		.cluster("xxx:2181,xxx:2181,122.xxx:2181")
		.node("/flume/client")
		.connect()
		.upload();
	}
}

class GetResource{
	public static String getPath(String fileName){
		return GetResource.class.getClassLoader().getResource(fileName).getPath();
	
	}
}
