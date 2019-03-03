package POC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class User implements Callable{
	String nom;
	String comportement;
	Serveur pere;
	String transacEnCours;
	Calculette fils;
	int delai;
	List<String> bloc;
	ExecutorService e;
	
	public User(String nom, String comportement, Serveur pere, int delai) {
		this.nom = nom;
		this.comportement = comportement;
		this.pere = pere;
		this.delai = delai;
	}
	
	@Override
	public User call() throws Exception {
		int i = 0;
		bloc = new ArrayList<>();
		while (i < 3){
			bloc.add(pere.liste[i]);
			i++;
		}
		// J'ai mon bloc, je délgue a la la calculette
		fils = new Calculette(new ArrayList<String>(bloc),this,delai);
		e = Executors.newSingleThreadExecutor();
		Future<Calculette> f = e.submit(fils); // On lance le thread de la calculette
		while (!f.isDone()) { // Tant que c'est pas fini on attend le résultat.
			if (e.isShutdown()) { // Qd un autre user à trouvé le résultat avant nous, le serveur va shutdown e
				f.cancel(false); // Annulation du travail de la calculette
				System.out.println("cancelled "+nom);
			}
		}
		if (!f.isCancelled())
			majBlocs(f);
		return this;
		}

	/** On ajoute le bloc à la chaîne globale et on attend de nouveaux mots **/
	private synchronized void majBlocs(Future<Calculette> f) {
		Calculette ca;
		try {
			ca = f.get(); // On récupère notre calculette pour avoir accès à son bloc miné
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(nom+" : travail terminé, je notifie le boss");
	}
		 		 
	}
