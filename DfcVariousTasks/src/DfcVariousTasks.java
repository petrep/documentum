import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfValue;

public class DfcVariousTasks {

	/**
	 * @param args
	 */
	private static final String UserName = "Administrator";
	// private static final String UserName = "dmadmin";
	private static final String Password = "password";
	// private static final String DocBase = "MyRepo";
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
	static String dql;
	static IDfQuery query;

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

	static void queryDQL() throws Exception {
		try {
//			dql = "SELECT object_name, r_object_id, r_modify_date from dm_document where folder('" + DIRPATH + "');";
			dql = "SELECT stamp, item_id, item_name, name, sent_by, task_name, date_sent, priority FROM dm_queue WHERE name = 'test1';";
			query = new DfQuery();
			query.setDQL(dql);
			System.out.println(dql);
			// Note:
			// the item_id is the r_object_id of the document in the inbox and the 
			// stamp is the r_object_id of the dmi_queue_item that represents the work item

			// Execute the query and recover the results
			collection = query.execute(session, DfQuery.DF_READ_QUERY);

			// Loop through the results
			while (collection.next()) {
//				IDfId id = collection.getId("r_object_id");
				String stamp = collection.getString("stamp");
				String item_id = collection.getString("item_id");
				String item_name = collection.getString("item_name");
				String name = collection.getString("name");
				String task_name = collection.getString("task_name");
//				if(name.equalsIgnoreCase("test1")){
//					System.out.println("equals to test1");
//				}
				String sent_by = collection.getString("sent_by");
				
				// String name = collection.getString("object_name");
				// int size = collection.getInt("r_content_size");
				System.out.println(stamp + " " + item_id + " " + item_name + " " + name + " " + sent_by + " " + task_name);

			}
		} catch (DfException e) {
			System.err.println("" + e.getLocalizedMessage());
			e.printStackTrace(System.err);

		} finally {
			// Close the IDfCollection.
			// This is extremely important, and hence is called
			// inside a finally{} block.
			if (collection != null) {
				collection.close();
			}
		}
	}

	static void delegetaTask() throws Exception {

	}

	static void getAllTasksinInbox() throws Exception {
		System.out.println("testtt");
		System.out.println("dbmsname: " + session.getDBMSName());
		System.out.println("server version: " + session.getServerVersion());
		System.out.println("username: " + session.getUser("test1"));
		IDfUser myUser = session.getUser("test");
		// myUser.getUserOSName();
		System.out.println("events: " + session.getEvents().getStateEx());
	}

	static void listAllWorkflows() throws Exception {
		// Get a list of all installed workflows!
		System.out.println("The following workflows are ready to run:-");
		IDfCollection Workflows = session.getRunnableProcesses("");
		while (Workflows.next()) {
			IDfTypedObject Next = Workflows.getTypedObject();

			// The following chunk of code has to de with
			// obtaining the attributes of the
			// IDfTypedObject we got back!

			java.util.Enumeration e = Next.enumAttrs();

			while (e.hasMoreElements()) {
				IDfAttr NextAttr = (IDfAttr) e.nextElement();

				System.out.print(NextAttr.getName() + " = ");

				int AttrCount = 0;
				if (NextAttr.isRepeating())
					AttrCount = Next.getValueCount(NextAttr.getName());
				else
					AttrCount = 1;

				for (int i = 0; i < AttrCount; i++) {
					// Get the next value!
					IDfValue NextAttrValue = Next.getRepeatingValue(
							NextAttr.getName(), i);
					System.out.print(NextAttrValue);
					System.out.print(", ");
				}
			}
			System.out.println();
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

	public static void main(String[] args) throws Exception {

		obtainDocbaseMap();
		createNewSession();
		restartTask();
		queryDQL();
		delegetaTask();
		getAllTasksinInbox();
		// listAllWorkflows();
		closeSession();

	}
}
