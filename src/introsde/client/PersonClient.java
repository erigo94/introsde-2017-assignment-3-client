package introsde.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.ws.Holder;

import introsde.assignment3.soap.ws.Activity;
import introsde.assignment3.soap.ws.People;
import introsde.assignment3.soap.ws.PeopleService;
import introsde.assignment3.soap.ws.Person;

public class PersonClient {

	private static String start;
	private static String info;
	private static String result;
	private static PrintStream print;
	private static String doc;
	private static PeopleService service;
	private static People people;
	private static int request;

	public static void main(String[] args) throws Exception {
		initialize();
		getURI();
		String type = "Social";
		int first = request1();
		printResult();
		Person person = request2(first); 
		printResult(); 
		request3(person);
		printResult(); 
		int delete = request4(); 
		printResult(); 
		request5(delete);
		printResult();
		int activityId = request6(first, type); 
		printResult(); 
		request7();
		printResult(); 
		request8(first, activityId); 
		printResult(); 
		Activity activity = request9(first); 
		printResult(); 
		activity = request10(first, activity.getIdActivity()); 
		printResult();
		request11(first, activity, 10); 
		printResult();
		request12(first); 
		printResult();
		
		System.out.println("All the requests were executed and the results were written in the client.log!");
	}

	public static void getURI() {
		PrintStream stream = print;
		stream.println("Server WSDL url: https://introsde-2017-assign-3-server.herokuapp.com/ws/people?wsdl");
	}

	private static void initialize() throws FileNotFoundException {
		FileOutputStream file = new FileOutputStream("client.log");
		print = new PrintStream(file);
		service = new PeopleService();
		people = service.getPeopleImplPort();
		people.init();
		request = 1;
	}

	private static void printResult() throws TransformerException {
		System.out.println("Request #" + request + " was written in the log file");
		PrintStream stream = print;
		stream.println(start);
		stream.println("\tInfo: " + info);
		stream.println("\tResult: " + result);
		stream.println();
		stream.println(doc);
		stream.println("************************************");
		stream.println();
		request++;
	}

	private static String printPerson(Person p) {
		String res = "Person with ID: " + p.getIdPerson() + "\n";
		res += "+-- Firstname: " + p.getFirstname() + "\n";
		res += "|-- Lastname: " + p.getLastname() + "\n";
		res += "|-- Birthdate: " + p.getBirthdate() + "\n";
		res += "|-- Activities: \n";
		for(Activity a : p.getActivities().getActivities()) {
			res += printActivity(a) + "\n";
		}
		return res;
	}

	private static String printActivity(Activity a) {
		String res = "\t+-- Activity with Id: " + a.getIdActivity() + "\n";
		res += "\t|-- Activity Name: " + a.getName() + "\n";
		res += "\t|-- Activity Description: " + a.getDescription() + "\n";
		res += "\t|-- Place : " + a.getPlace() + "\n";
		res += "\t|-- Start date: " + a.getStartdate() + "\n";
		res += "\t|-- Rate: " + a.getRate() + "\n";
		res += "\t|-- Activity Type: " + a.getActivityType().getActivityType() + "\n";
		return res;
	}

	// List of requests

	// Request #1 - Return the first id
	private static int request1() {
		start = "Request #1: readPersonList()";
		info = "return the list of all the people in the database";
		doc = "";
		List<Person> pList = people.readPersonList();
		if (!pList.isEmpty()) {
			result = "OK, list has " + pList.size() + " elements";
			for (int i = 0; i < pList.size() - 1; i++) {
				doc += printPerson(pList.get(i)) + "------------------\n";
			}
			doc += printPerson(pList.get(pList.size() - 1));
			return pList.get(0).getIdPerson();
		} else
			result = "ERROR, list is EMPTY";
		return -1;

	}

	// Request #2 - return the information of the Person with given id
	private static Person request2(int id) {
		start = "Request #2: readPerson(Long id)";
		info = "return the personal information and the current Activitys of the first Person in db (id=" + id + ")";
		Person p = people.readPerson(Long.valueOf(id));
		if (p != null)
			result = "OK, Found Person by id =" + id;
		else
			result = "ERROR, Didn't find any Person with  id = " + id;
		doc = printPerson(p);
		return p;
	}

	// Request #3
	private static void request3(Person p) {
		start = "Request #3: updatePerson(Person p)";
		info = "update the Personal information of the Person returned in request #2 with a different firstname and return it";
		String newFirstname = "Antonio";
		p.setFirstname(newFirstname);
		Holder<Person> holder = new Holder<Person>(p);
		people.updatePerson(holder);
		Person serverAnswer = holder.value;
		if (newFirstname.equals(serverAnswer.getFirstname()))
			result = "OK, The firstname is changed";
		else
			result = "ERROR, The firstname is NOT changed";
		doc = printPerson(serverAnswer);
	}

	// Request #4 - return the id of the new Person
	private static int request4() {
		start = "Request #4: createPerson(Person p)";
		info = "create a new Person with the personal information and current healtprofile and return it";
		Person p = new Person();
		p.setFirstname("Luca");
		p.setLastname("Grigi");
		p.setBirthdate("1996-10-08");
		p.setActivities(null);
		
		Activity a = new Activity();
		a.setName("Tennis");
		a.setDescription("Playing tennis with my friends");
		a.setPlace("Santa Chiara Tennis fields");
		a.setStartdate("2017-12-13T16:00:00.0");
		a.setRate(8);
		a.setIdActivityType(2); //Sport
		
		Person.Activities pa = new Person.Activities();
		pa.getActivities().add(a);
		p.setActivities(pa);
		
		Holder<Person> holder = new Holder<Person>(p);
		people.createPerson(holder);
		Person serverAnswer = holder.value;
		if (serverAnswer != null)
			result = "OK, Create person with id = " + p.getIdPerson();
		else
			result = "ERROR, Didn't create the Person";
		doc = printPerson(serverAnswer);
		return serverAnswer.getIdPerson();
	}

	// Request #5 - remove the person created in request 4
	private static void request5(int id) {
		start = "Request #5: deletePerson(Long id)";
		info = "delete the Person created in the request #4 with id = " + id;
		int ris = people.deletePerson(Long.valueOf(id));
		if (ris == 0)
			result = "OK,the person with id " + id + " was deleted ";
		else
			result = "ERROR, Didn't delete the person with  id = " + id;
	}
	
	// Request #6
	private static int request6(int id, String activityType) {
		start = "Request #6: readPersonPreferences(Long id, String ActivityType)";
		info = "return the list of preferences of " + activityType + " for Person with id = " + id;
		doc = "";
		List<Activity> list = people.readPersonPreferences(Long.valueOf(id), activityType);
		for (int i = 0; i < list.size(); i++) {
			doc += printActivity(list.get(i));
		}
		if (!list.isEmpty()) {
			result = "OK,the person has " + list.size() + " weight ";
			return list.get(0).getIdActivity();
		}

		else
			result = "ERROR, the person hasn't Activity of weight";
		return 0;

	}

	// Request #7
	private static void request7() {
		start = "Request #7: readPreferences()";
		info = "return the list of all Activitys in the database";
		doc = "";
		List<Activity> list = people.readPreferences();
		for (int i = 0; i < list.size(); i++) {
			doc += printActivity(list.get(i));
		}
		if (!list.isEmpty())
			result = "OK, there are " + list.size() + " Activity in the database ";
		else
			result = "ERROR, the person hasn't Activity of weight";
	}

	// Request #8
	private static void request8(int id, int activityId) {
		start = "Request #8: readPersonPreferences(Long id, Long activityId)";
		info = "return the Activity with activityId = " + activityId + " for Person with id = " + id;
		doc = "";
		Activity m = people.readPersonPreferencesById(Long.valueOf(id), Long.valueOf(activityId));
		doc = printActivity(m);
		if (m != null)
			result = "OK, Found Activity by mid =" + activityId;
		else
			result = "ERROR, Didn't find any Activity with  mid = " + activityId;
	}

	// Request #9
	private static Activity request9(int id) {
		start = "Request #9: savePersonPreferences(Long id, Activity a)";
		info = "save a new Activity of Person identified with id=" + id + " and archive the old value in the history";
		doc = "";
		Activity a = new Activity();
		a.setName("Volleyball");
		a.setDescription("Playing volleybal with my friends");
		a.setPlace("Mattarello Sport Center");
		a.setStartdate("2017-12-14T21:00:00.0");
		a.setRate(7);
		a.setIdActivityType(1); //Social
		
		Holder<Activity> holderActivity = new Holder<Activity>(a);
		people.savePersonPreferences(Long.valueOf(id), holderActivity);
		a = holderActivity.value;
		doc = printActivity(a);
		if (a != null) {
			result = "OK, New Activity with mid =" + a.getIdActivity();
			return a;
		} else
			result = "ERROR, Didn't create any Activity ";
		return null;
	}

	// Request #10
	private static Activity request10(int id, int idActivity) {
		String newName = "Go Karting";
		start = "Request #10: updatePersonPreferences(Long id, Activity a)";
		info = "update the value of the Activity created in request #9, the new name is " + newName;
		doc = "";
		Activity a = new Activity();
		a.setIdActivity(idActivity);
		a.setName(newName);
		a.setDescription("Go karting at Affi");
		a.setPlace("Affi center");
		a.setStartdate("2017-12-16T22:00:00.0");
		a.setRate(9);
		a.setIdActivityType(2); //Sport
		
		Holder<Activity> holderActivity = new Holder<Activity>(a);
		people.updatePersonPreferences(Long.valueOf(id), holderActivity);
		doc = printActivity(holderActivity.value);
		if (holderActivity.value.getName().equals(newName)) {
			result = "OK, the firstname is changed at " + newName;
			return holderActivity.value;
		}
		else {
			result = "ERROR, the firstame isn't changed ";
			return holderActivity.value;
		}
	}
	
	// Request #11
	private static void request11(int id, Activity activity, int value) {
		start = "Request #11: evaluatePersonPreferences(Long id, Activity a, int value)";
		info = "Update the rate of the Activity created in request #9, the new rate is " + value;
		doc = "";
		Holder<Activity> holderActivity = new Holder<Activity>(activity);
		people.evaluatePersonPreferences(Long.valueOf(id), holderActivity, value);
		doc = printActivity(holderActivity.value);
		if (holderActivity.value.getRate() == value)
			result = "OK, the rate is changed at " + String.valueOf(value);
		else
			result = "ERROR, the rate isn't changed ";
	}
	
	// Request #12
	private static void request12(int id) {
		start = "Request #12: getBestPersonPreference(Long id)";
		info = "Return the best list of Preference for the Person with id = " + String.valueOf(id);
		doc = "";
		List<Activity> list = people.getBestPersonPreference(Long.valueOf(id));
		for (int i = 0; i < list.size(); i++) {
			doc += printActivity(list.get(i));
		}
		if (!list.isEmpty())
			result = "OK, there are " + list.size() + " Activity in the database ";
		else
			result = "ERROR, the person hasn't Activity of weight";
	}
}
