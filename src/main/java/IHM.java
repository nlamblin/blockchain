import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class IHM implements Runnable{
	@Override
	public void run() {
		while (true) {
			Scanner sc = new Scanner(System.in);
			ArrayList <Trader> a = new ArrayList<Trader>(Server.traders.values());
			System.out.print("Id du trader: ");
			int idTrader = sc.nextInt();
			
			if (idTrader == -2) {
				System.out.println("Shutting down...");
				Server.serverShutdown();
				break;
			}
			
			
			System.out.println("Informations for "+a.get(idTrader).getName());
			
			System.out.println("Inputs:\n"+
					"1 - Display money sent by a given trader\n"
					+ "2 - Display money sent at a given time");

			int choice = sc.nextInt();
			 
			if (choice == 1) {
				System.out.println(Chain.getInstance().getHistorybySender(a.get(idTrader)));
			}
			
			else if (choice == 2) {
				int y,m,d,h,min;
				Calendar c = Calendar.getInstance();
				y = c.get(Calendar.YEAR);
				m = c.get(Calendar.MONTH)+1;
				d = c.get(Calendar.DAY_OF_MONTH);
				h = c.get(Calendar.HOUR_OF_DAY);
				System.out.print("Minute: ");
				min = sc.nextInt();
				System.out.print("Second: ");
				int s = sc.nextInt();
				//System.out.println("Date: "+d+" "+m+" "+y+"  "+h+" "+min+" "+s);
				System.out.println(Chain.getInstance().findTransaction(a.get(idTrader), y, m, d, h, min, s));
			}
			
			
			
		}
		
	}
    
    
}
