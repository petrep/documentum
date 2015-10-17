import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;

public class DfcRestartTask {

	/**
	 * @param args
	 */
	private static final String UserName = "Administrator";
	// private static final String UserName = "dmadmin";
	private static final String Password = "password";
//	private static final String DocBase = "MyRepo";
	private static final String DocBase = "documentum";
	private static final String DIRPATH = "/Temp/sample";

	static IDfClientX clientx;
	static IDfClient client;
	static IDfDocbaseMap myMap;
	static IDfSessionManager sMgr;
	static IDfSession session;
	static DfLoginInfo loginInfoObj;

	static IDfFolder folder;
	static IDfCollection collection;
	static IDfDocument doc;

	static void obtainDocbaseMap() throws Exception {
		clientx = new DfClientX();
		client = clientx.getLocalClient();
		myMap = client.getDocbaseMap();
		System.out.println("Docbases for Docbroker: " + myMap.getHostName());
		System.out.println("Total number of Docbases: "
				+ myMap.getDocbaseCount());
		for (int i = 0; i < myMap.getDocbaseCount(); i++) {
			System.out.println("Docbase " + (i + 1) + ": "
					+ myMap.getDocbaseName(i));
		}
	}

	static void createNewSession() throws Exception {
		try {
			clientx = new DfClientX();
			client = clientx.getLocalClient();
			loginInfoObj = new DfLoginInfo();
			loginInfoObj.setUser(UserName);
			loginInfoObj.setPassword(Password);
			sMgr = client.newSessionManager();
			sMgr.setIdentity(DocBase, loginInfoObj);
			session = sMgr.newSession(DocBase);
			if (session != null && session.isConnected()) {
				System.out.println("docbase is connected successfully ");
				System.out.println("Connected to repository name: "
						+ session.getDocbaseName());
				System.out.println("Connected to repository ID  : "
						+ session.getDocbaseId());
			}
		} catch (Throwable e) {
			System.err.println("" + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}

	static void restartTask() throws Exception {

		int count = 0;
		try {
			folder = session.getFolderByPath(DIRPATH);
			collection = folder.getContents("r_object_id");
			while (collection.next()) {
				count++;
				IDfId id = collection.getId("r_object_id");
				doc = (IDfDocument) session.getObject(id);
				System.out.println("Object Name: " + doc.getObjectName());
			}
			if (session != null) {
				System.out.println("Successful");
			} else {
				System.out.println("Unsuccessful");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (collection != null) {
                collection.close();
            }
		}
	}
	
	static void delegetaTask() throws Exception {
		
	}

	static void closeSession() throws Exception {
		System.out.println("docbase will now be disconnected ");
		sMgr.release(session);
		try {
			if (session != null && session.isConnected()) {
				System.out.println("Still connected to repository");
				System.out.println("Connected to repository name: "
						+ session.getDocbaseName());
				System.out.println("Connected to repository ID  : "
						+ session.getDocbaseId());
			}
		} catch (DfException e) {
			System.err.println("" + e.getLocalizedMessage());
			e.printStackTrace(System.err);
		}
	}
	
	public static void main(String[] args) throws Exception {

		obtainDocbaseMap();
		createNewSession();
		restartTask();
		delegetaTask();
		closeSession();

	}
}
