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
		/*
		 * Person person = request2(first); printResult(); request3(person);
		 * printResult(); int delete = request4(); printResult(); request5(delete);
		 * printResult(); int mid = request6(first, type); printResult(); request7();
		 * printResult(); request8(first, type, mid); printResult(); Activity Activity =
		 * request9(first); printResult(); request10(first, Activity); printResult();
		 */
		System.out.println("All the requests were executed and the results were written in the client.log!");
	}

	public static void getURI() {
		PrintStream stream = print;
		//stream.println("Server WSDL url: https://introsde2016-assignment3.herokuapp.com/ws/people?wsdl");
	}

	private static void initialize() throws FileNotFoundException {
		FileOutputStream file = new FileOutputStream("client.log");
		print = new PrintStream(file);
		service = new PeopleService();
		people = service.getPeopleImplPort();
		request = 1;
	}

	private static void printResult() throws TransformerException {
		System.out.println("Request #" + request + " was written in the log file");
		PrintStream stream = print;
		stream.println(start);
		stream.println("--> Info: " + info);
		stream.println("--> Result: " + result);
		stream.println();
		stream.println(doc);
		stream.println("************************************");
		stream.println();
		request++;
	}

	private static String printPerson(Person p) {
		String res = "Person with ID: " + p.getIdPerson() + "\n";
		res += "--> Firstname: " + p.getFirstname() + "\n";
		res += "--> Lastname: " + p.getLastname() + "\n";
		res += "--> Birthdate: " + p.getBirthdate() + "\n";
		res += "--> Activities: \n";
		for(Activity a : p.getActivities()) {
			res += printActivity(a) + "\n";
		}
		return res;
	}

	private static String printActivity(Activity a) {
		String res = "\tActivity with ID=" + a.getIdActivity() + "\n";
		res += "\t--> Activity Name: " + a.getName() + "\n";
		res += "\t--> Activity Description: " + a.getDescription() + "\n";
		res += "\t--> Place : " + a.getPlace() + "\n";
		res += "\t--> Start date: " + a.getStartdate() + "\n";
		res += "\t--> Rate: " + a.getRate() + "\n";
		res += "\t--> Activity Type: " + a.getActivityType().getActivityType() + "\n";
		return res;
	}

	// List of request

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
/*
	// Request #2 - return the information of the Person with given id
	private static Person request2(int id) {
		start = "Request #2: readPerson(int id)";
		info = "return the personal information and the current Activitys of the first Person in db (id=" + id + ")";
		Person p = people.readPerson(id);
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
		info = "update the Personal information of the Person returned in request #2 with an extra F in the last name and return it";
		String newLastname = p.getLastname() + "F";
		p.setCurrentHealth(null); // because the update method should modify only the personal information, not
									// the health profile
		p.setLastname(newLastname); // modify the lastname with the current plus one F
		Holder<Person> holder = new Holder<Person>(p);
		people.updatePerson(holder);
		Person ris = holder.value;
		if (newLastname.equals(ris.getLastname()))
			result = "OK, The lastname is changed";
		else
			result = "ERROR, The lastname is NOT changed";
		doc = printPerson(ris);
	}

	// Request #4 - return the id of the new Person
	private static int request4() {
		start = "Request #4: createPerson(Person p)";
		info = "create a new Person with the personal information and current healtprofile and return it";
		Person p = new Person();
		p.setName("Miky");
		p.setLastname("Test");
		p.setEmail("michele@test.it");
		p.setBirthdate("07/08/1993");
		Activity m = new Activity();
		m.setType("weight");
		m.setValue("176");
		m.setValueType("integer");
		m.setDate("01/12/2016");
		Person.CurrentHealth cp = new Person.CurrentHealth();
		cp.getActivity().add(m);
		p.setCurrentHealth(cp);
		Holder<Person> holder = new Holder<Person>(p);
		people.createPerson(holder);
		p = holder.value;
		if (p != null)
			result = "OK, Create person with id =" + p.getIdPerson();
		else
			result = "ERROR, Didn't create the Person";
		doc = printPerson(p);
		return p.getIdPerson();
	}

	// Request #5 - remove the person created in request 4
	private static void request5(int id) {
		start = "Request #5: deletePerson(int id)";
		info = "cancel the Person created in the request #4 with id=" + id;
		int ris = people.deletePerson(id);
		if (ris == 0)
			result = "OK,the person with id " + id + " was deleted ";
		else
			result = "ERROR, Didn't delete the person with  id = " + id;
	}

	// Request #6
	private static int request6(int id, String type) {
		start = "Request #6: readPersonHistory(Long id, String ActivityType)";
		info = "return the list of values (the history) of " + type + " for Person with id=" + id;
		doc = "";
		List<Activity> list = people.readPersonHistory(id, type);
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
		start = "Request #7: readActivityTypes()";
		info = "return the list of all Activitys in the database";
		doc = "";
		List<Activity> list = people.readActivityTypes();
		for (int i = 0; i < list.size(); i++) {
			doc += printActivity(list.get(i));
		}
		if (!list.isEmpty())
			result = "OK, there are " + list.size() + " Activity in the database ";
		else
			result = "ERROR, the person hasn't Activity of weight";
	}

	// Request #8
	private static void request8(int id, String type, int mid) {
		start = "Request #8: readPersonActivity(Long id, String ActivityType, Long mid)";
		info = "return the Activity with mid=" + mid + " and type=" + type + " for Person with id=" + id;
		doc = "";
		Activity m = people.readPersonActivity(id, type, mid);
		doc = printActivity(m);
		if (m != null)
			result = "OK, Found Activity by mid =" + mid;
		else
			result = "ERROR, Didn't find any Activity with  mid = " + mid;
	}

	// Request #9
	private static Activity request9(int id) throws ParseException_Exception {
		start = "Request #9: savePersonActivity(Long id, Activity m)";
		info = "save a new Activity of Person identified with id=" + id + " and archive the old value in the history";
		doc = "";
		Activity m = new Activity();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		m.setDate(sdf.format(new Date()));
		m.setType("height");
		m.setValue("180");
		m.setValueType("integer");
		Holder<Activity> Activity = new Holder<Activity>(m);
		people.savePersonActivity(id, Activity);
		m = Activity.value;
		doc = printActivity(m);
		if (m != null) {
			result = "OK, New Activity with mid =" + m.getIdActivity();
			return m;
		} else
			result = "ERROR, Didn't create any Activity ";
		return null;
	}

	// Request #10
	private static void request10(int id, Activity m) {
		String newValue = String.valueOf(Integer.parseInt(m.getValue()) + 5);
		start = "Request #10: updatePersonActivity(Long id, Activity m)";
		info = "update the value of the Activity created in request #9, the new value is " + newValue;
		doc = "";
		m.setValue(newValue);
		Holder<Activity> Activity = new Holder<Activity>(m);
		people.updatePersonActivity(id, Activity);
		m = Activity.value;
		doc = printActivity(m);
		if (newValue.equals(m.getValue()))
			result = "OK, the value is changed at " + newValue;
		else
			result = "ERROR, the value isn't changed ";
	}*/
}
