package POC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Calculette implements Callable{

	User pere;
	int delai; // Pour ralentir volontairement
	List<String> bloc; // Bloc  = liste de mots à miner
	List<Integer> res;
	
	public Calculette(List<String> bloc, User pere, int delai) {
		this.pere = pere;
		this.delai = delai;
		this.bloc = bloc;
		res = new ArrayList<Integer>();
	}

	@Override
	/*
	 * Représente le minage. Parcours toute la liste de mots, et pour chaque mots 
	 * on compte le nombre de char. en attendant und élai aléatoire pour simuler la vitesse.
	 * Une fois fini, on retourne l'objet qui permettra à l'user possédant cette calculette 
	 * d'écrire dans la blockchain.
	 */
	public Calculette call() throws Exception {
		for (int i = 0 ; i < 3 ; i++) {
			String mot = bloc.get(i);			
			for (int j = 0 ; j < mot.length() ; j++) {
				int a = ((int)(Math.random()*1000))+delai;
				Thread.sleep(a);
				//System.out.println(pere.nom+": "+mot+": "+j+"("+mot.charAt(j)+")");
			}
			res.add(new Integer(mot.length()));
		}
		return this;
	}
	
}
