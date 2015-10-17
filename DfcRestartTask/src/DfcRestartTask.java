import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;

public class DfcRestartTask {

	/**
	 * @param args
	 */
	// private static final String UserName = "Administrator";
	private static final String UserName = "dmadmin";
	private static final String Password = "password";
	private static final String DocBase = "MyRepo";
	private static final String DIRPATH = "/Temp/sample";

	static IDfClientX clientx;
	static IDfClient client;
	static IDfDocbaseMap myMap;
	static IDfSessionManager sMgr;
	static IDfSession session;
	static DfLoginInfo loginInfoObj;

	public static void main(String[] args) throws Exception {

		// IDfSessionManager sMgr = null;
		// IDfSession session = null;

		// IDfFolder folder = null;
		// IDfClient client = null;
		// IDfCollection collection = null;
		// IDfDocument doc = null;
		// int count = 0;

		// try {
		// client = DfClient.getLocalClient();
		// IDfLoginInfo loginInfo = new DfLoginInfo();
		//
		// loginInfo.setUser(UserName);
		// loginInfo.setPassword(Password);
		// loginInfo.setDomain("");
		//
		// IDfSession docbase_session = client.newSession(DocBase, loginInfo);
		// folder = docbase_session.getFolderByPath(DIRPATH);
		//
		// collection = folder.getContents("r_object_id");
		// while(collection.next()) {
		// count++;
		// IDfId id = collection.getId("r_object_id");
		// doc = (IDfDocument) docbase_session.getObject(id);
		// System.out.println("Object Name: " + doc.getObjectName());
		// }
		//
		// if(docbase_session != null) {
		// System.out.println("Successful");
		// }
		// else {
		// System.out.println("Unsuccessful");
		// }
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }

		obtainDocbaseMap();
		createNewSession();
		closeSession();

		// try {
		//
		// IDfLoginInfo loginInfo = new DfLoginInfo();
		//
		// loginInfo.setUser(UserName);
		// loginInfo.setPassword(Password);
		// loginInfo.setDomain("");
		//
		// IDfSession docbase_session = client.newSession(DocBase, loginInfo);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// ;

	}

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
			loginInfoObj.setUser("dmadmin");
			loginInfoObj.setPassword("password");
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
}
