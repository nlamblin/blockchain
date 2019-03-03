package POC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serveur {

	String[] liste = {"toto","to","tototo"}; // LISTE DE TRANSACTION DE BASE
	List<Callable<User>> users;
	static ExecutorService executorServiceUsers; 
	List<Callable<User>> usersEnCours;
	List<List<Integer>> blocsRes; // équivalent blockchain
	
	
	public Serveur () {
		executorServiceUsers = Executors.newFixedThreadPool(2); // Pool d'users
		blocsRes = new ArrayList<>();
		users = new ArrayList<>();
		User u = new User("Rapide", null, this,0);
		User u1 = new User("Lent", null, this,5000);
		User u2 = new User("Moyen", null, this,1500);
		users.add(u);
		users.add(u1);  
		usersEnCours = new ArrayList<Callable<User>>(users);
		
		//users.add(u2);
	}
	
	public void go() {
		try {
			/*
			 * Lancement des users autonomes...
			 */
		//ROUND 1 
			User fini = executorServiceUsers.invokeAny(users); // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
			blocsRes.add(fini.fils.res); // Ajout du bloc miné par l'utilisateur
			usersEnCours.remove(fini);
			for (Callable<User> u : usersEnCours) { // Shutdown des autres utilisateurs en passant par leur exécuteur
				User au = (User)u;
				au.e.shutdownNow();
			}
		
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * NOUVELLE LISTE DE TRANSACTION 
		 * 
		 */
		liste[0]="ttt";
		liste[1]="ttoot";
		liste[2]="tatata";
		
		// 
		/* 
		 * Round 2
		 * évidement on peut faire une boucle mais là c plus facile pour débugger
		 */
		System.out.println("Début du round 2");
		try {
			User fini = executorServiceUsers.invokeAny(users);
			blocsRes.add(fini.fils.res);
			usersEnCours.remove(fini);
			for (Callable<User> u : usersEnCours) {
				User au = (User)u;
				au.e.shutdownNow();
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Blockchain finale (lol) "+blocsRes);
		
	}
}
