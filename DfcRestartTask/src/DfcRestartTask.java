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
import com.documentum.fc.client.IDfQueueItem;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfWorkflow;
import com.documentum.fc.client.IDfWorkitem;
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
		String dql = "select  item_id from dmi_queue_item where task_name in ( 'doc3')";
		IDfQuery query = new DfQuery();
		query.setDQL(dql);
		collection = query.execute(session, IDfQuery.DF_READ_QUERY);
		try {
			while (collection.next()) {
				IDfWorkitem wi = (IDfWorkitem) session.getObject(collection
						.getId("item_id")); // selecting from the dmi_workitem
											// table
				
				IDfId queueItemId = wi.getQueueItemId();
				// selecting from the dmi_queue_item table
				IDfQueueItem qi = (IDfQueueItem) session.getObject(queueItemId);

				int task_state = wi.getRuntimeState();//checking the task_state

				switch (task_state) {

				case 0:
					System.out.println("Dormant");
					break;
				case 1:
					System.out.println("Acquired");
					break;
				case 2:
					System.out.println("Finished");
					break;
				case 3:
					System.out.println("Paused");
					break;
				default:
					System.out.println("No runtime state found");
				}

				if (task_state == 0) {
					//wi.acquire();
					System.out.println("Acquiring item");
					//wi.delegateTask("test1");
					System.out.println("delegating dormant task with " + "task name " + qi.getTaskName());
					// dont acquire if the task is already acquired, just delegate it
				} else if (task_state == 1) {
					//wi.delegateTask("test3");
					System.out.println("delegating acquired task with " + "task name " + qi.getTaskName());
				}
				System.out.println("task name" + qi.getTaskName()
						+ " Performer " + qi.getName());
				System.out.println("task state " + qi.getTaskState());
				//****if the task has already been acquired, shows acquired, achieved by wi.acquire();
				System.out.println("Item Name: " + qi.getItemName());
				// System.out.println("performer " + wi.getPerformerName() +
				// " Activities "+ wi.getActivity());
			}
		} finally {
			if (collection != null) {
				collection.close();
			}
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
		// restartTask();
		delegetaTask();
		closeSession();

	}
}
