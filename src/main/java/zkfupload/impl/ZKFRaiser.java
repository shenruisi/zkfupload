package zkfupload.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZKFRaiser.newRaiser()
		.filePath(GetResource.getPath(<the file you want to upload>))
		.cluster(<cluster list>) /xxx:2181,xxx:2181,xxx:2181
		.node(<your node path>) /flume/xxx
		.watcher(<create your zookeepr watcher>)
		.connect()
		.upload();
 */
public class ZKFRaiser {
	private ZooKeeper zk;
	private String filePath;
	private String clusterList;
	private String nodePath;
	private int sessionTimeout = 3000;
	private Watcher watcher;
	
	private ZKFRaiser(){}
	
	public static ZKFRaiser newRaiser(){
		return new ZKFRaiser();
	}
	
	/**
	 *以逗号分隔 
	 */
	public ZKFRaiser cluster(String clusterList){
		this.clusterList = clusterList;
		return this;
	}
	
	public ZKFRaiser filePath(String filePath){
		this.filePath = filePath;
		return this;
	}
	
	public ZKFRaiser node(String nodePath){
		this.nodePath = nodePath;
		return this;
	}
	
	public ZKFRaiser sessionTimeout(int sessionTimeout){
		this.sessionTimeout = sessionTimeout;
		return this;
	}
	
	public ZKFRaiser watcher(Watcher watcher){
		this.watcher = watcher;
		return this;
	}
	
	public ZKFRaiser connect() throws Exception{
		if (this.filePath == null || (this.filePath != null && this.filePath.length() == 0)) throw new Exception("file path 为空");
		if (this.nodePath == null || (this.nodePath != null && this.nodePath.length() == 0)) throw new Exception("node path 为空");
		if (this.clusterList == null || (this.clusterList != null && this.clusterList.length() == 0)) throw new Exception("cluster list 为空");
		
		this.zk = new ZooKeeper(this.clusterList,this.sessionTimeout,this.watcher);
		
		return this;
	}
	
	public void upload() {
		InputStream is = null; 
		byte[] data = null;
		ByteArrayOutputStream bytestream = null;
		try {  
	        is = new FileInputStream(this.filePath);  
	        bytestream = new ByteArrayOutputStream();  
	        int ch;  
	        while ((ch = is.read()) != -1) {  
	            bytestream.write(ch);  
	        }  
	        data = bytestream.toByteArray();  
	        System.out.println(new String(data));  
	    } catch (FileNotFoundException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        try {  
	            bytestream.close();  
	            is.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
		
		try {
			_rNode(data);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void _rNode(byte[] data) throws KeeperException, InterruptedException{
		String[] splitNodes = this.nodePath.split("/");
		StringBuilder nodePath = new StringBuilder();
		for (int i = 0; i < splitNodes.length; i++){
			String node = splitNodes[i];
			if (node.length() == 0) continue;
			nodePath.append("/" + node);
			_createNodeIfNeeded(nodePath.toString(),(i == splitNodes.length - 1) ? data : null);
		}
	}
	
	private void _createNodeIfNeeded(String nodeName, byte[] data) throws KeeperException, InterruptedException{
		if (this.zk == null) return;
		
		if (this.zk.exists(nodeName, false) == null){
			this.zk.create(nodeName, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
	
}
